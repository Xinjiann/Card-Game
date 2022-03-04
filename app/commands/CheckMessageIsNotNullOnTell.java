package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.assertNotNull;

public class CheckMessageIsNotNullOnTell implements DummyTell {

	@Override
	public void tell(ObjectNode message) {
		assertNotNull(message);
	}

}
