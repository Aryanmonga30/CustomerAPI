package com.api.customer.dao;

import com.api.customer.configuration.HibernateConfiguration;
import com.api.customer.model.CustomerModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;


@Repository
public class  CustomerDaoImplementation implements CustomerDao{
    //Defining a logger
    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerDaoImplementation.class);

    HibernateConfiguration customerConfiguration;
    @Autowired
    SessionFactory sessionFactory;

    public List<CustomerModel> getCustomers(int pageNo){
        Session session=sessionFactory.openSession();
        int pageSize= 5;
        try {
            int offset = (pageNo - 1) * pageSize;
            String query = "FROM CustomerModel";
            Query hqlquery = session.createQuery(query).setFirstResult(offset).setMaxResults(pageSize);
            return hqlquery.getResultList();
        }
        catch (Exception e){
            logger.info(e.getMessage());
            return null;
        }
        finally {
            session.close();
        }
    }
    @Override
    public CustomerModel getCustomerById(int id) {
        EntityManager entityManager=null;
        try {
            entityManager = sessionFactory.createEntityManager();
            String hql="FROM CustomerModel WHERE id = :ID";
            Query query = entityManager.createQuery(hql);
            query.setParameter("ID", id);
            CustomerModel customer = (CustomerModel) query.getSingleResult();
            return customer;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
        finally {
            entityManager.close();
        }

    }

    @Override
    @Transactional
    public CustomerModel addCustomer(CustomerModel customer) {
        Session session=sessionFactory.openSession();
        Transaction transaction= session.getTransaction();
        try {
            transaction.begin();
            session.saveOrUpdate(customer);
            transaction.commit();
            return customer;
        } catch (Exception e) {
            logger.info(e.getMessage());
            transaction.rollback();
            return null;
        }
        finally {
            session.close();
        }

    }
    public CustomerModel getCustomerByCode(String customerCode) {
        EntityManager entityManager = null;
        try {
             entityManager = sessionFactory.createEntityManager();
             String hql ="FROM CustomerModel WHERE customerCode= :CUSTOMERCODE";
            Query query = entityManager.createQuery(hql);
            query.setParameter("CUSTOMERCODE", customerCode);
            CustomerModel customer = (CustomerModel) query.getSingleResult();
            return customer;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }
    public List<CustomerModel> getCustomersByClientId(int client){
        EntityManager entityManager=null;
        try {
            entityManager = sessionFactory.createEntityManager();
            String hql="FROM CustomerModel WHERE client = :CLIENT";
            Query query = entityManager.createQuery(hql);
            query.setParameter("CLIENT", client);
            List<CustomerModel> customers = query.getResultList();
            return customers;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
        finally {
            entityManager.close();
        }
    }



}
