/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * The persistent class for the DeviceMConfig database table.
 *
 */
@Entity
@Table(name="DeviceMConfig")
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class DeviceMConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
        private long id;

        private String keyword;

        private String value;

        //bi-directional many-to-one association to Device
        @ManyToOne
        @JsonIgnore
        private Device device;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof DeviceMConfig && obj !=null) {
                        return getId() == ((DeviceMConfig)obj).getId();
                }
                return super.equals(obj);
        }

	public DeviceMConfig() {
	}

        public long getId() {
                return this.id;
        }

        public void setId(long id) {
                this.id = id;
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

        public Device getDevice() {
                return this.device;
        }

        public void setDevice(Device device) {
                this.device = device;
        }
}
