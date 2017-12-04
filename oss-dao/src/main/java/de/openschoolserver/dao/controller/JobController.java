/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.*;
import de.openschoolserver.dao.tools.OSSShellTools;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

public class JobController extends Controller {
	
	Logger logger = LoggerFactory.getLogger(JobController.class);
	
    static FileAttribute<Set<PosixFilePermission>> privatDirAttribute  = PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rwx------"));
    static FileAttribute<Set<PosixFilePermission>> privatFileAttribute = PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rw-------"));
	
	private static String basePath = "/home/groups/SYSADMINS/jobs/";

	public JobController(Session session) {
		super(session);
	}
	
	public OssResponse createJob(Job job) {
		String scheduledTime = "now";
		if( job.isPromptly() ) {
			job.setStartTime(new Timestamp(System.currentTimeMillis()));
		} else {
			Date date = new Date(job.getStartTime().getTime());
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm yyyy-MM-dd");
			scheduledTime        = fmt.format(date);
		}

		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(job);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("createJob" + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}

		/*
		 * Write the file
		 */

		StringBuilder path = new StringBuilder(basePath);
		Long instituteId   = 0L;
		if( job.getCephalixInstitute() != null ) {
			instituteId = job.getCephalixInstitute().getId();
		}
		path.append(String.valueOf(instituteId)).append("/").append(String.valueOf(instituteId));
		File jobDir = new File( path.toString() );
		try {
			Files.createDirectories(jobDir.toPath(), privatDirAttribute );
			path.append("/").append(String.valueOf(job.getId()));
			Path jobFile     = Paths.get(path.toString());
			List<String> tmp =  new ArrayList<String>();
			tmp.add("( /usr/share/oss/tools/oss_date.sh");
			tmp.add(job.getCommand());
			tmp.add("/usr/share/oss/tools/oss_date.sh) &> " + path.toString()+ ".log");
			Files.write(jobFile, tmp );
		} catch (Exception e) {
			logger.error("createJob" + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		String[] program   = new String[4];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "at";
		program[1] = "-f";
		program[2] = path.toString();
		program[3] = scheduledTime;
		OSSShellTools.exec(program, reply, error, null);
		logger.debug("create job  : " + path.toString() + " : " + job.getCommand());
		return null;
	}
	
	public List<Job> findJobByDescription(String description, Timestamp after, Timestamp befor) {
		List<Job> jobs   = new ArrayList<Job>();
		EntityManager em = getEntityManager();
		
		return jobs;
	}

}
