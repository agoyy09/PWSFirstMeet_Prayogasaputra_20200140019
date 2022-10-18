/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pabd.learnmigratedb;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author asus
 */
@Entity
@Table(name = "favorite")
@NamedQueries({
    @NamedQuery(name = "Favorite.findAll", query = "SELECT f FROM Favorite f"),
    @NamedQuery(name = "Favorite.findByFavorite", query = "SELECT f FROM Favorite f WHERE f.favorite = :favorite"),
    @NamedQuery(name = "Favorite.findByIdUser", query = "SELECT f FROM Favorite f WHERE f.idUser = :idUser")})
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "favorite")
    private Integer favorite;
    @Basic(optional = false)
    @Column(name = "id_user")
    private String idUser;
    @JoinColumn(name = "id_doa", referencedColumnName = "id_doa")
    @OneToOne(optional = false)
    private Doa idDoa;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "favorite")
    private User user;

    public Favorite() {
    }

    public Favorite(Integer favorite) {
        this.favorite = favorite;
    }

    public Favorite(Integer favorite, String idUser) {
        this.favorite = favorite;
        this.idUser = idUser;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Doa getIdDoa() {
        return idDoa;
    }

    public void setIdDoa(Doa idDoa) {
        this.idDoa = idDoa;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (favorite != null ? favorite.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Favorite)) {
            return false;
        }
        Favorite other = (Favorite) object;
        if ((this.favorite == null && other.favorite != null) || (this.favorite != null && !this.favorite.equals(other.favorite))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pabd.learnmigratedb.Favorite[ favorite=" + favorite + " ]";
    }
    
}
