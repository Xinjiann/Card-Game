package structures.basic.abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// bind card name and corresponding ability
public class AbilityToCard {

	public static HashMap<String, ArrayList<Ability>> abilityToCard = new HashMap<String, ArrayList<Ability>>();

	public static void init() {

		// spells
		// deck 1
		abilityToCard.put("Truestrike", loadAbilities(new Truestrike()));
		abilityToCard.put("Sundrop Elixir", loadAbilities(new SundropElixir()));
		// deck 2
		abilityToCard.put("Staff of Y'Kir'", loadAbilities(new StaffOfYkir()));
		abilityToCard.put("Entropic Decay", loadAbilities(new EntropicDecay()));

		// units
		// deck 1
		abilityToCard.put("Pureblade Enforcer", loadAbilities(new growWhenCastSpell()));
		abilityToCard.put("Azure Herald", loadAbilities(new addAvatarHealth()));
		abilityToCard.put("Silverguard Knight", loadAbilities(new addAttackWhenAvatarBeAttack(), new Provoke()));
		abilityToCard.put("Azurite Lion", loadAbilities(new AttackTwice()));
		abilityToCard.put("Fire Spitter", loadAbilities(new UnlimitedAttackRange()));
		abilityToCard.put("Ironcliff Guardian", loadAbilities(new SummonAnywhere(), new Provoke()));
		// deck 2
		abilityToCard.put("Serpenti", loadAbilities(new AttackTwice()));
		abilityToCard.put("Planar Scout", loadAbilities(new SummonAnywhere()));
		abilityToCard.put("Pyromancer", loadAbilities(new UnlimitedAttackRange()));
		abilityToCard.put("Blaze Hound", loadAbilities(new DrawCardWhenSummon()));
		abilityToCard.put("WindShrike", loadAbilities(new Flying(), new DrawCardOnDeath()));
		abilityToCard.put("Rock Pulveriser", loadAbilities(new Provoke()));
	}

	private static ArrayList<Ability> loadAbilities(Ability... abilities) {
		return new ArrayList<>(Arrays.asList(abilities));
	}

}
