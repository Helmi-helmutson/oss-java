package de.openschoolserver.dao;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="Responses")
@NamedQueries({
	@NamedQuery(name="Response.findAll", query="SELECT r FROM Response r"),
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Response implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    long id;

	private String code;
	private String text;
	
	@ManyToOne
	Session session;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof Response && obj !=null) {
                        return getId() == ((Response)obj).getId();
                }
                return super.equals(obj);
        }
	
	public Response(Session session,String code, String text){
		this.session = session;
		this.code = code;
		this.text = text;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getCode() {
		return this.code;
	}

}
