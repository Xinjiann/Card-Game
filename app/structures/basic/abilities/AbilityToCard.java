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

		// deck 2
		abilityToCard.put("Serpenti", loadAbilities(new AttackTwice()));
		abilityToCard.put("Planar Scout", loadAbilities(new SummonAnywhere()));
		abilityToCard.put("Pyromancer", loadAbilities(new UnlimitedAttackRange()));
		abilityToCard.put("Blaze Hound", loadAbilities(new DrawCardWhenSummon()));
		abilityToCard.put("WindShrike", loadAbilities(new Flying(), new DrawCardOnDeath()));
		abilityToCard.put("Rock Pulveriser", loadAbilities(new Provoke()));
	}

	private  static ArrayList<Ability> loadAbilities(Ability ... abilities) {
		return new ArrayList<>(Arrays.asList(abilities));
	}

}
