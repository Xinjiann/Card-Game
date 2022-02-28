package structures.basic.abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AbilityToCard {

	public static HashMap<String, ArrayList<Ability>> abilityToCard = new HashMap<String, ArrayList<Ability>>();


	public static void init() {

		// spells
		abilityToCard.put("Truestrike", loadAbilities(new Truestrike()));

		// units

	}

	private  static ArrayList<Ability> loadAbilities(Ability ... abilities) {
		return new ArrayList<>(Arrays.asList(abilities));
	}

}