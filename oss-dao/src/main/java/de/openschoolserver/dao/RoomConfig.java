/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the RoomConfig database table.
 *
 */
@Entity
@Table(name="RoomConfig")
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class RoomConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
	    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
        private long id;

        private String keyword;

        private String value;

        //bi-directional many-to-one association to Room
        @ManyToOne
        @JsonIgnore
        private Room room;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof RoomConfig && obj !=null) {
                        return getId() == ((RoomConfig)obj).getId();
                }
                return super.equals(obj);
        }

	    public RoomConfig() {
	    }

	    public RoomConfig(Room room, String keyword, String value) {
	    	this.room    = room;
	    	this.keyword = keyword;
	    	this.value   = value;
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

        public Room getRoom() {
                return this.room;
        }

        public void setRoom(Room room) {
                this.room = room;
        }
}
