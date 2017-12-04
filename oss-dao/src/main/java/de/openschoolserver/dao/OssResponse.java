/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Responses database table.
 * 
 */
@Entity
@Table(name="Responses")
@NamedQuery(name="OssResponse.findAll", query="SELECT r FROM OssResponse r")
public class OssResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="RESPONSES_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RESPONSES_ID_GENERATOR")
	private long id;

	private String code;
	private String value;
	
	/*
	 * This id will be set to the id of a object which was created or deleted or manipulated if any
	 */
	private Long   objectId;
	
	@ManyToOne
    Session session;

	@Column(name="session_id", insertable = false, updatable = false)
	private java.math.BigInteger sessionId;
	
    @Override
    public boolean equals(Object obj) {
            if (obj instanceof OssResponse && obj !=null) {
                    return getId() == ((OssResponse)obj).getId();
            }
            return super.equals(obj);
    }

    public OssResponse() {
    }

    public OssResponse(Session session,String code, String value){
            this.session  = session;
            this.code     = code;
            this.value    = value;
            this.objectId = null;
    }
   
    public OssResponse(Session session,String code, String value, Long objectId){
        this.session  = session;
        this.code     = code;
        this.value    = value;
        this.objectId = objectId;
    }

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getObjecId() {
		return this.objectId;
	}

	public void setObjectId(Long id) {
		this.objectId = id;
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