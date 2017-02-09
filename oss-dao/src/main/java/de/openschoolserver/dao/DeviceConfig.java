/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;


import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * The persistent class for the DeviceConfig database table.
 *
 */
@Entity
public class DeviceConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
        private long id;

        private String keyword;

        private String value;

        //bi-directional many-to-one association to Device
        @ManyToOne
        @JsonIgnore
        private Device device;

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
