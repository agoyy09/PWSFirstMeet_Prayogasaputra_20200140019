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
import pabd.learnmigratedb.exceptions.IllegalOrphanException;
import pabd.learnmigratedb.exceptions.NonexistentEntityException;
import pabd.learnmigratedb.exceptions.PreexistingEntityException;

/**
 *
 * @author asus
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Favorite favoriteOrphanCheck = user.getFavorite();
        if (favoriteOrphanCheck != null) {
            User oldUserOfFavorite = favoriteOrphanCheck.getUser();
            if (oldUserOfFavorite != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Favorite " + favoriteOrphanCheck + " already has an item of type User whose favorite column cannot be null. Please make another selection for the favorite field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Favorite favorite = user.getFavorite();
            if (favorite != null) {
                favorite = em.getReference(favorite.getClass(), favorite.getFavorite());
                user.setFavorite(favorite);
            }
            em.persist(user);
            if (favorite != null) {
                favorite.setUser(user);
                favorite = em.merge(favorite);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getIdUser()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getIdUser());
            Favorite favoriteOld = persistentUser.getFavorite();
            Favorite favoriteNew = user.getFavorite();
            List<String> illegalOrphanMessages = null;
            if (favoriteNew != null && !favoriteNew.equals(favoriteOld)) {
                User oldUserOfFavorite = favoriteNew.getUser();
                if (oldUserOfFavorite != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Favorite " + favoriteNew + " already has an item of type User whose favorite column cannot be null. Please make another selection for the favorite field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (favoriteNew != null) {
                favoriteNew = em.getReference(favoriteNew.getClass(), favoriteNew.getFavorite());
                user.setFavorite(favoriteNew);
            }
            user = em.merge(user);
            if (favoriteOld != null && !favoriteOld.equals(favoriteNew)) {
                favoriteOld.setUser(null);
                favoriteOld = em.merge(favoriteOld);
            }
            if (favoriteNew != null && !favoriteNew.equals(favoriteOld)) {
                favoriteNew.setUser(user);
                favoriteNew = em.merge(favoriteNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = user.getIdUser();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Favorite favorite = user.getFavorite();
            if (favorite != null) {
                favorite.setUser(null);
                favorite = em.merge(favorite);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
