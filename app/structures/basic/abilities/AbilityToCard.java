package structures.basic.abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AbilityToCard {

	public static HashMap<String, ArrayList<Ability>> abilityToCard = new HashMap<String, ArrayList<Ability>>();


	public static void init() {

		// spells
		// deck 1
		abilityToCard.put("Truestrike", loadAbilities(new Truestrike()));
		// deck 2
		abilityToCard.put("Staff of Y'Kir'", loadAbilities(new StaffOfYkir()));
		
		// units
		// deck 1
		abilityToCard.put("Silverguard Knight", loadAbilities(new Provoke()));
		// deck 2
		abilityToCard.put("Serpenti", loadAbilities(new AttackTwice()));
	}

	private  static ArrayList<Ability> loadAbilities(Ability ... abilities) {
		return new ArrayList<>(Arrays.asList(abilities));
	}

}
