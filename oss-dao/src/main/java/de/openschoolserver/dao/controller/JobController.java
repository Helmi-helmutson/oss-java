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
import javax.persistence.Query;

public class JobController extends Controller {

	Logger logger = LoggerFactory.getLogger(JobController.class);

    static FileAttribute<Set<PosixFilePermission>> privatDirAttribute  = PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rwx------"));
    static FileAttribute<Set<PosixFilePermission>> privatFileAttribute = PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rw-------"));

	private static String basePath = "/home/groups/SYSADMINS/jobs/";

	public JobController(Session session) {
		super(session);
	}

	public Job getById(Long jobId) {
		EntityManager em = getEntityManager();
		try {
			Job job = em.find(Job.class, jobId);
			Path JOB_COMMAND = Paths.get(basePath + String.valueOf(jobId));
			Path JOB_RESULT  = Paths.get(basePath + String.valueOf(jobId) + ".log");
			List<String> tmp = Files.readAllLines(JOB_COMMAND);
			job.setCommand(String.join(getNl(),tmp));
			tmp = Files.readAllLines(JOB_RESULT);
			job.setResult(String.join(getNl(),tmp));
			return job;
		} catch (Exception e) {
			logger.error("DeviceId:" + jobId + " " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	/**
	 * Creates a new job
	 * @param job The job to be created.
	 * @return The result in an OssResponse object
	 * @see OssResponse
	 */
	public OssResponse createJob(Job job) {

		if( job.getDescription().length() > 128 ) {
			job.setDescription(job.getDescription().substring(0, 127));
		}
		/*
		 * Set job start time
		 */
		String scheduledTime = "now";
		if( job.isPromptly() ) {
			job.setStartTime(new Timestamp(System.currentTimeMillis()));
		} else {
			Date date = new Date(job.getStartTime().getTime());
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm yyyy-MM-dd");
			scheduledTime        = fmt.format(date);
		}

		/*
		 * Create the Job entity
		 */
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
		File jobDir = new File( path.toString() );
		try {
			Files.createDirectories(jobDir.toPath(), privatDirAttribute );
			path.append(String.valueOf(job.getId()));
			Path jobFile     = Paths.get(path.toString());
			List<String> tmp =  new ArrayList<String>();
			tmp.add("( /usr/share/oss/tools/oss_date.sh");
			tmp.add(job.getCommand());
			tmp.add("E=$?");
			tmp.add("oss_api.sh PUT system/jobs/"+String.valueOf(job.getId())+"/exit/$E");
			tmp.add("echo $E");
			tmp.add("/usr/share/oss/tools/oss_date.sh) &> " + path.toString()+ ".log");
			Files.write(jobFile, tmp );
		} catch (Exception e) {
			logger.error("createJob" + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}

		/*
		 * Start the job
		 */
		String[] program   = new String[4];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "at";
		program[1] = "-f";
		program[2] = path.toString();
		program[3] = scheduledTime;
		OSSShellTools.exec(program, reply, error, null);
		logger.debug("create job  : " + path.toString() + " : " + job.getCommand());
		return new OssResponse(this.getSession(),"OK","Job was created successfully",job.getId());
	}

	public OssResponse setExitCode(Long jobId, Integer exitCode) {
		EntityManager em = getEntityManager();
		try {
			Job job = em.find(Job.class, jobId);
			job.setExitCode(exitCode);
			job.setEndTime(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.merge(job);
			em.getTransaction().commit();
		}  catch (Exception e) {
			logger.error("createJob" + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Jobs exit code was set successfully");
	}

	public OssResponse restartJob(Long jobId) {
		EntityManager em = getEntityManager();
		try {
			Job job = em.find(Job.class, jobId);
			job.setStartTime(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.merge(job);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("createJob" + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		String[] program   = new String[4];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "at";
		program[1] = "-f";
		program[2] = basePath + String.valueOf(jobId);
		program[3] = "now" ;
		OSSShellTools.exec(program, reply, error, null);
		return new OssResponse(this.getSession(),"OK","Job was restarted successfully",jobId);
	}

	@SuppressWarnings("unchecked")
	public List<Job> searchJobs(String description, Timestamp after, Timestamp befor) {
		EntityManager em = getEntityManager();
		Query query = null;
		if( after.equals(befor) ) {
			query = em.createNamedQuery("Job.getByDescription").setParameter("description", description);
		} else {
			query = em.createNamedQuery("Job.getByDescriptionAndTime")
					.setParameter("description", description)
					.setParameter("after", after)
					.setParameter("befor", befor);
		}
		List<Job> jobs =  query.getResultList();
		em.close();
		return jobs;
	}

	@SuppressWarnings("unchecked")
	public List<Job> getRunningJobs() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Job.getRunning");
		List<Job> jobs =  query.getResultList();
		em.close();
		return jobs;
	}

	@SuppressWarnings("unchecked")
	public List<Job> getFailedJobs() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Job.getFailed");
		List<Job> jobs =  query.getResultList();
		em.close();
		return jobs;
	}

	@SuppressWarnings("unchecked")
	public List<Job> getSucceededJobs() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Job.getSucceeded");
		List<Job> jobs =  query.getResultList();
		em.close();
		return jobs;
	}
}
