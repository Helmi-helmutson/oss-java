/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the RoomMConfig database table.
 *
 */
@Entity
public class RoomMConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
        private long id;

        private String key;

        private String value;

        //bi-directional many-to-one association to Room
        @ManyToOne
        private Room room;

        public long getId() {
                return this.id;
        }

        public void setId(long id) {
                this.id = id;
        }

        public String getKey() {
                return this.key;
        }

        public void setKey(String key) {
                this.key = key;
        }

        public String getValue() {
                return this.value;
        }

        public void setValue(String value) {
                this.value = value;
        }

        public Room getRoom() {
                return this.room;
        }

        public void setRoom(Room room) {
                this.room = room;
        }
}
