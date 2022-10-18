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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author asus
 */
@Entity
@Table(name = "doa")
@NamedQueries({
    @NamedQuery(name = "Doa.findAll", query = "SELECT d FROM Doa d"),
    @NamedQuery(name = "Doa.findByIdDoa", query = "SELECT d FROM Doa d WHERE d.idDoa = :idDoa"),
    @NamedQuery(name = "Doa.findByNamadoa", query = "SELECT d FROM Doa d WHERE d.namadoa = :namadoa"),
    @NamedQuery(name = "Doa.findByBahasaarab", query = "SELECT d FROM Doa d WHERE d.bahasaarab = :bahasaarab"),
    @NamedQuery(name = "Doa.findByBahasalatin", query = "SELECT d FROM Doa d WHERE d.bahasalatin = :bahasalatin"),
    @NamedQuery(name = "Doa.findByTerjemahan", query = "SELECT d FROM Doa d WHERE d.terjemahan = :terjemahan")})
public class Doa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_doa")
    private String idDoa;
    @Basic(optional = false)
    @Column(name = "Nama_doa")
    private String namadoa;
    @Basic(optional = false)
    @Column(name = "Bahasa_arab")
    private String bahasaarab;
    @Basic(optional = false)
    @Column(name = "Bahasa_latin")
    private String bahasalatin;
    @Basic(optional = false)
    @Column(name = "Terjemahan")
    private String terjemahan;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "idDoa")
    private Favorite favorite;

    public Doa() {
    }

    public Doa(String idDoa) {
        this.idDoa = idDoa;
    }

    public Doa(String idDoa, String namadoa, String bahasaarab, String bahasalatin, String terjemahan) {
        this.idDoa = idDoa;
        this.namadoa = namadoa;
        this.bahasaarab = bahasaarab;
        this.bahasalatin = bahasalatin;
        this.terjemahan = terjemahan;
    }

    public String getIdDoa() {
        return idDoa;
    }

    public void setIdDoa(String idDoa) {
        this.idDoa = idDoa;
    }

    public String getNamadoa() {
        return namadoa;
    }

    public void setNamadoa(String namadoa) {
        this.namadoa = namadoa;
    }

    public String getBahasaarab() {
        return bahasaarab;
    }

    public void setBahasaarab(String bahasaarab) {
        this.bahasaarab = bahasaarab;
    }

    public String getBahasalatin() {
        return bahasalatin;
    }

    public void setBahasalatin(String bahasalatin) {
        this.bahasalatin = bahasalatin;
    }

    public String getTerjemahan() {
        return terjemahan;
    }

    public void setTerjemahan(String terjemahan) {
        this.terjemahan = terjemahan;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDoa != null ? idDoa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doa)) {
            return false;
        }
        Doa other = (Doa) object;
        if ((this.idDoa == null && other.idDoa != null) || (this.idDoa != null && !this.idDoa.equals(other.idDoa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pabd.learnmigratedb.Doa[ idDoa=" + idDoa + " ]";
    }
    
}
