import java.io.*;
import java.util.*;


public class Main {
	public Scanner input = new Scanner(System.in);

	public void run(InputStream in) {
		input = new Scanner(in);
		while (true) {
			int protocolNo = chooseStrategy();
			if (protocolNo == 0) {
				System.exit(0); // terminate with success (user asked for it)
			}
			Warden warden = createWarden();
			Protocol protocol = createProtocol(protocolNo, warden.getNumberOfPrisoners());
			int iterationCount = warden.hasPredeterminedHistory()? 1 : getIterationCount();

			SimulationResult sim = Protocol.runSimulation(warden, protocol, iterationCount);
			System.out.println("Atlag:   " + sim.getAvgDays() + " nap, azaz " + sim.getAvgYears() + " ev");
			System.out.println("Minimum: " + sim.getMinDays() + " nap, azaz " + sim.getMinYears() + " ev");
			System.out.println("Maximum: " + sim.getMaxDays() + " nap, azaz " + sim.getMaxYears() + " ev");
		}
	}

	private int chooseStrategy() {
		return readIntBetween("Valaszhato strategiak:"
				+ "\n1. Egy lampaoltogato"
				+ "\n2. Egy lampaoltogato, okos rabok"
				+ "\n3. Dinamikusan valasztott lampaoltogato"
				+ "\n4. Ketfazisu szamlalas (ujrainditassal)"
				+ "\n5. Ketfazisu szamlalas"
				+ "\n0. Kilepes"
				+ "\nMelyik strategiat hasznaljuk?", 0, 5);
	}

	private Warden createWarden() {
		int selection = readIntBetween("Rabok sorrendje:"
				+ "\n1. Veletlen sorrendben"
				+ "\n2. Elore meghatarozott sorrendben (fajlbol olvasva)"
				+ "\nMilyen modon valasszuk a rabok sorrendjet?", 1, 2);
		if (selection == 1) {
			return createWardenFromCount();
		} else {
			return createWardenFromFile();
		}
	}

	private Warden createWardenFromCount() {
		int prisonerCount = readIntBetween("Hany rab legyen?", 1, Integer.MAX_VALUE);
		return new Warden(prisonerCount);
	}

	private Warden createWardenFromFile() {
		while (true) {
			System.out.print("Mi a fajl neve? ");
			try {
				String fileName = input.next();
				List<Integer> history = readFile(fileName);
				return new Warden(history);
			} catch (FileNotFoundException e) {
				System.err.print("Rossz fajlnev! Fajl neve ujra: ");
			} catch (IOException e) {
				System.err.print("Rossz fajl! Fajl neve ujra: ");
			}
		}
	}

	private Protocol createProtocol(int protocolNo, int n) {
		switch (protocolNo) {
			case 1:
				return new ProtocolSingleCounter();
			case 2:
				return new ProtocolSCWithSmartDrones();
			case 3:
				return new ProtocolDynamicCounter();
			case 4: {
				int stage1Length = readIntBetween("Hany napos legyen az elso szakasz?", 1, Integer.MAX_VALUE);
				int stage2Length = readIntBetween("Hany napos legyen a masodik szakasz?", 1, Integer.MAX_VALUE);
				int bulkSize = readIntFrom("Mekkora lepesekben szamoljon a foszamlalo?", Utilities.getDivisors(n));
				return new ProtocolBulkWithRestart(stage1Length, stage2Length, bulkSize);
			}
			case 5: {
				int stage1Length = readIntBetween("Hany napos legyen az elso szakasz?", 1, Integer.MAX_VALUE);
				int stage2Length = readIntBetween("Hany napos legyen a masodik szakasz?", 1, Integer.MAX_VALUE);
				int bulkSize = readIntFrom("Mekkora lepesekben szamoljon a foszamlalo?", Utilities.getDivisors(n));
				return new ProtocolBulkWithLoop(stage1Length, stage2Length, bulkSize);
			}
			default:
				throw new IllegalArgumentException("Nem ertelmezheto protokoll: " + protocolNo);
		}
	}

	private int getIterationCount() {
		return readIntBetween("Hanyszor alkalmazzuk?", 0, Integer.MAX_VALUE);
	}

	private int readIntBetween(String message, int min, int max) {
		System.out.println(message + " [" + min + " - " + max + "]: ");
		int result;
		do {
			while (!input.hasNextInt()) {
				input.next();
			}
			result = input.nextInt();
		} while (!(min <= result && result <= max));
		return result;
	}

	private int readIntFrom(String message, List<Integer> divisors) {
		System.out.println(message + " " + divisors + ": ");
		int result;
		do {
			while (!input.hasNextInt()) {
				input.next();
			}
			result = input.nextInt();
		} while (!divisors.contains(result));
		return result;
	}

	private static List<Integer> readFile(String source) throws IOException {
		ArrayList<Integer> history = new ArrayList<>();
		try (BufferedReader input = new BufferedReader(new FileReader(source))) {
			String line;
			while ((line = input.readLine()) != null) {
				int prisoner = Integer.parseInt(line);
				history.add(prisoner);
			}
		}
		return history;
	}

	public static void main(String[] args) {
		//new Main().run(System.in);
		SimulationResult sim = Protocol.runSimulation(new Warden(100), new ProtocolBadgesAndCrowns(), 100000);
		System.out.println("atlag: " + sim.getAvgDays());
		System.out.println("min: " + sim.getMinDays());
		System.out.println("max: " + sim.getMaxDays());
		//SwingGUI.main(args);
	}
}
