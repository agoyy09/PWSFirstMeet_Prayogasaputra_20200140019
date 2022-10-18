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
public class FavoriteJpaController implements Serializable {

    public FavoriteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("pabd_learnmigratedb_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public FavoriteJpaController() {
    }
    

    public void create(Favorite favorite) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Doa idDoaOrphanCheck = favorite.getIdDoa();
        if (idDoaOrphanCheck != null) {
            Favorite oldFavoriteOfIdDoa = idDoaOrphanCheck.getFavorite();
            if (oldFavoriteOfIdDoa != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Doa " + idDoaOrphanCheck + " already has an item of type Favorite whose idDoa column cannot be null. Please make another selection for the idDoa field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doa idDoa = favorite.getIdDoa();
            if (idDoa != null) {
                idDoa = em.getReference(idDoa.getClass(), idDoa.getIdDoa());
                favorite.setIdDoa(idDoa);
            }
            User user = favorite.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getIdUser());
                favorite.setUser(user);
            }
            em.persist(favorite);
            if (idDoa != null) {
                idDoa.setFavorite(favorite);
                idDoa = em.merge(idDoa);
            }
            if (user != null) {
                Favorite oldFavoriteOfUser = user.getFavorite();
                if (oldFavoriteOfUser != null) {
                    oldFavoriteOfUser.setUser(null);
                    oldFavoriteOfUser = em.merge(oldFavoriteOfUser);
                }
                user.setFavorite(favorite);
                user = em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFavorite(favorite.getFavorite()) != null) {
                throw new PreexistingEntityException("Favorite " + favorite + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Favorite favorite) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite persistentFavorite = em.find(Favorite.class, favorite.getFavorite());
            Doa idDoaOld = persistentFavorite.getIdDoa();
            Doa idDoaNew = favorite.getIdDoa();
            User userOld = persistentFavorite.getUser();
            User userNew = favorite.getUser();
            List<String> illegalOrphanMessages = null;
            if (idDoaNew != null && !idDoaNew.equals(idDoaOld)) {
                Favorite oldFavoriteOfIdDoa = idDoaNew.getFavorite();
                if (oldFavoriteOfIdDoa != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Doa " + idDoaNew + " already has an item of type Favorite whose idDoa column cannot be null. Please make another selection for the idDoa field.");
                }
            }
            if (userOld != null && !userOld.equals(userNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain User " + userOld + " since its favorite field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idDoaNew != null) {
                idDoaNew = em.getReference(idDoaNew.getClass(), idDoaNew.getIdDoa());
                favorite.setIdDoa(idDoaNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getIdUser());
                favorite.setUser(userNew);
            }
            favorite = em.merge(favorite);
            if (idDoaOld != null && !idDoaOld.equals(idDoaNew)) {
                idDoaOld.setFavorite(null);
                idDoaOld = em.merge(idDoaOld);
            }
            if (idDoaNew != null && !idDoaNew.equals(idDoaOld)) {
                idDoaNew.setFavorite(favorite);
                idDoaNew = em.merge(idDoaNew);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                Favorite oldFavoriteOfUser = userNew.getFavorite();
                if (oldFavoriteOfUser != null) {
                    oldFavoriteOfUser.setUser(null);
                    oldFavoriteOfUser = em.merge(oldFavoriteOfUser);
                }
                userNew.setFavorite(favorite);
                userNew = em.merge(userNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = favorite.getFavorite();
                if (findFavorite(id) == null) {
                    throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite favorite;
            try {
                favorite = em.getReference(Favorite.class, id);
                favorite.getFavorite();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            User userOrphanCheck = favorite.getUser();
            if (userOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Favorite (" + favorite + ") cannot be destroyed since the User " + userOrphanCheck + " in its user field has a non-nullable favorite field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Doa idDoa = favorite.getIdDoa();
            if (idDoa != null) {
                idDoa.setFavorite(null);
                idDoa = em.merge(idDoa);
            }
            em.remove(favorite);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Favorite> findFavoriteEntities() {
        return findFavoriteEntities(true, -1, -1);
    }

    public List<Favorite> findFavoriteEntities(int maxResults, int firstResult) {
        return findFavoriteEntities(false, maxResults, firstResult);
    }

    private List<Favorite> findFavoriteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Favorite.class));
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

    public Favorite findFavorite(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Favorite.class, id);
        } finally {
            em.close();
        }
    }

    public int getFavoriteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Favorite> rt = cq.from(Favorite.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
