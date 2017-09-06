/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;


import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * The persistent class for the OSSMConfir database table.
 *
 */
@Entity
@Table(name="OSSMConfig")
@NamedQueries({
        @NamedQuery(name="OSSMConfig.getAll", query="SELECT c FROM OSSMConfig c WHERE c.objectType = :type AND c.objectId := id"),
        @NamedQuery(name="OSSMConfig.get",    query="SELECT c FROM OSSMConfig c WHERE c.objectType = :type AND c.objectId := id AND c.keyword := key"),
        @NamedQuery(name="OSSMConfig.check",  query="SELECT c FROM OSSMConfig c WHERE c.objectType = :type AND c.objectId := id AND c.keyword := key AND c.value := value")
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class OSSMConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
        private long id;

        private String objectType;

        private Long  objectId;

        private String keyword;

        private String value;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof OSSMConfig && obj !=null) {
                        return getId() == ((OSSMConfig)obj).getId();
                }
                return super.equals(obj);
        }

        public OSSMConfig() {
	}

        public long getId() {
                return this.id;
        }

        public void setId(long id) {
                this.id = id;
        }

        public String getObjectType() {
                return this.objectType;
        }

        public void setObjectType(String objectType) {
                this.objectType = objectType;
        }

        public Long getObjectId() {
                return this.objectId;
        }

        public void setObjectId(Long objectId) {
                this.objectId = objectId;
        }

        public String getKeyword() {
                return this.keyword;
        }

        public void setKeyword(String keyword) {
                this.keyword = keyword;
        }

        public String getValue() {
                return this.value;
        }

        public void setValue(String value) {
                this.value = value;
        }

}
