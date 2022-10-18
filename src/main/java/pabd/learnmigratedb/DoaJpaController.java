/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pabd.learnmigratedb;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import pabd.learnmigratedb.exceptions.IllegalOrphanException;
import pabd.learnmigratedb.exceptions.NonexistentEntityException;
import pabd.learnmigratedb.exceptions.PreexistingEntityException;

/**
 *
 * @author asus
 */
public class DoaJpaController implements Serializable {

    public DoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("pabd_learnmigratedb_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public DoaJpaController() {
    }

    
    public void create(Doa doa) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite favorite = doa.getFavorite();
            if (favorite != null) {
                favorite = em.getReference(favorite.getClass(), favorite.getFavorite());
                doa.setFavorite(favorite);
            }
            em.persist(doa);
            if (favorite != null) {
                Doa oldIdDoaOfFavorite = favorite.getIdDoa();
                if (oldIdDoaOfFavorite != null) {
                    oldIdDoaOfFavorite.setFavorite(null);
                    oldIdDoaOfFavorite = em.merge(oldIdDoaOfFavorite);
                }
                favorite.setIdDoa(doa);
                favorite = em.merge(favorite);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDoa(doa.getIdDoa()) != null) {
                throw new PreexistingEntityException("Doa " + doa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doa doa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doa persistentDoa = em.find(Doa.class, doa.getIdDoa());
            Favorite favoriteOld = persistentDoa.getFavorite();
            Favorite favoriteNew = doa.getFavorite();
            List<String> illegalOrphanMessages = null;
            if (favoriteOld != null && !favoriteOld.equals(favoriteNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Favorite " + favoriteOld + " since its idDoa field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (favoriteNew != null) {
                favoriteNew = em.getReference(favoriteNew.getClass(), favoriteNew.getFavorite());
                doa.setFavorite(favoriteNew);
            }
            doa = em.merge(doa);
            if (favoriteNew != null && !favoriteNew.equals(favoriteOld)) {
                Doa oldIdDoaOfFavorite = favoriteNew.getIdDoa();
                if (oldIdDoaOfFavorite != null) {
                    oldIdDoaOfFavorite.setFavorite(null);
                    oldIdDoaOfFavorite = em.merge(oldIdDoaOfFavorite);
                }
                favoriteNew.setIdDoa(doa);
                favoriteNew = em.merge(favoriteNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = doa.getIdDoa();
                if (findDoa(id) == null) {
                    throw new NonexistentEntityException("The doa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doa doa;
            try {
                doa = em.getReference(Doa.class, id);
                doa.getIdDoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Favorite favoriteOrphanCheck = doa.getFavorite();
            if (favoriteOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Doa (" + doa + ") cannot be destroyed since the Favorite " + favoriteOrphanCheck + " in its favorite field has a non-nullable idDoa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(doa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doa> findDoaEntities() {
        return findDoaEntities(true, -1, -1);
    }

    public List<Doa> findDoaEntities(int maxResults, int firstResult) {
        return findDoaEntities(false, maxResults, firstResult);
    }

    private List<Doa> findDoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doa.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Doa findDoa(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doa.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doa> rt = cq.from(Doa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
