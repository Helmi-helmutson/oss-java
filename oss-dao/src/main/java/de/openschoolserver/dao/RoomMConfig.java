/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * The persistent class for the RoomMConfig database table.
 *
 */
@Entity
@Table(name="RoomMConfig")
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class RoomMConfig implements Serializable {
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
                if (obj instanceof RoomMConfig && obj !=null) {
                        return getId() == ((RoomMConfig)obj).getId();
                }
                return super.equals(obj);
        }

	public RoomMConfig() {
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
