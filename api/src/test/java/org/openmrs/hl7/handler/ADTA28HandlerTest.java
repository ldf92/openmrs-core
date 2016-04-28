/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.hl7.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptProposal;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Constants;
import org.openmrs.obs.ComplexObsHandler;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.app.MessageTypeRouter;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.parser.GenericParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO finish testing all methods ORUR01Handler
 */
public class ADTA28HandlerTest extends BaseContextSensitiveTest {

	protected static final String ADT_INITIAL_DATA_XML = "org/openmrs/hl7/include/ADTTest-initialData.xml";

	// hl7 parser to be used throughout
	protected static GenericParser parser = new GenericParser();

	private static MessageTypeRouter router = new MessageTypeRouter();

	static {
		router.registerApplication("ADT", "A28", new ADTA28Handler());
	}

	/**
	 * Run this before each unit test in this class. This adds the hl7 specific data to the initial
	 * and demo data done in the "@Before" method in {@link BaseContextSensitiveTest}.
	 *
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(ADT_INITIAL_DATA_XML);
	}

	/**
	 * @see ORUR01Handler#processMessage(Message)
	 */
	@Test
	@Verifies(value = "should create new patient from Hl7 message if not exist in the database", method = "processMessage(Message)")
	public void processMessage_shouldCreateNewPatientFromHl7MessageIfNotExistInTheDatabase() throws Exception {
		ObsService obsService = Context.getObsService();

		//This message add new patient identifier = 999 into database
		String hl7string = "MSH|^~\\&|FORMENTRY|ChariteSAP|HL7LISTENER|LOCAL|20091123101300^0|HUP|ADT^A28^ADT_A05|9166768|P|2.5|1|||AL||ASCII\r"
			+ "EVN|A28|20091123101300|||1\r"
			+ "PID|||999^^^Old Identification Number||Patient^Demo^OldId||20011114|M|||20371^02^2400^724||||||724^Y||||||02|||11|20371|724^DEUT^N||N";

		int oldSize = Context.getPatientService().getAllPatients().size();

		Message hl7message = parser.parse(hl7string);
		router.processMessage(hl7message);

		assertEquals(oldSize+1, Context.getPatientService().getAllPatients().size());
	}
}
