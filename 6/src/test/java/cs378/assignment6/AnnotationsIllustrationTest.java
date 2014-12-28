/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package cs378.assignment6;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

/**
 * Illustrates the use of Hibernate native APIs.  The code here is unchanged from the {@code basic} example, the
 * only difference being the use of annotations to supply the metadata instead of Hibernate mapping files.
 *
 * @author Steve Ebersole
 */
public class AnnotationsIllustrationTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
	}
	
	@Test
	public void testPositive () throws Exception {
		Client client = ClientBuilder.newClient();
		try {
			String response = client.target("http://localhost:8080/assignment6/myeavesdrop/projects/solum/meetings").request().get(String.class);
			assertNotNull(response);
		} catch (Exception e) {
			assertTrue (false);
		} finally {
			client.close();
		}
	}
	
	@Test
	public void testNegative () throws Exception {
		Client client = ClientBuilder.newClient();
		try {
			String response = client.target("http://localhost:8080/assignment6/myeavesdrop/projects/random/meetings").request().get(String.class);
			assertNull(response);
		} catch (Exception e) {
		} finally {
			client.close();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
}