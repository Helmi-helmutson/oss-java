package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Responses database table.
 * 
 */
@Entity
@Table(name="Responses")
@NamedQuery(name="Response.findAll", query="SELECT r FROM Response r")
public class Response implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="RESPONSES_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RESPONSES_ID_GENERATOR")
	private long id;

	private String code;
	private String value;
	@ManyToOne
    Session session;

	@Column(name="session_id")
	private java.math.BigInteger sessionId;
	
    @Override
    public boolean equals(Object obj) {
            if (obj instanceof Response && obj !=null) {
                    return getId() == ((Response)obj).getId();
            }
            return super.equals(obj);
    }

    public Response() {
    }

    public Response(Session session,String code, String value){
            this.session = session;
            this.code    = code;
            this.value   = value;
    }
   
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public java.math.BigInteger getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(java.math.BigInteger sessionId) {
		this.sessionId = sessionId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}