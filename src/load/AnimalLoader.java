package load;

import main.*;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class responsible for loading and initializing animals
 * from external data sources.
 */

public class AnimalLoader {
	
    private final SecureRandom random = new SecureRandom();

    public String[] getRandomFoodChain(Mode mode) throws IOException {
        String filename = switch (mode) {
            case PAST -> "data/past_animals.txt";
            case PRESENT -> "data/present_animals.txt";
            case FUTURE -> "data/future_animals.txt";
        };

        List<String[]> chains = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("Era:")) continue;
                if (line.startsWith("Food Chain")) {
                    String[] parts = line.split(":")[1].trim().split(",");
                    if (parts.length == 4) {
                        for (int i = 0; i < 4; i++) parts[i] = parts[i].trim();
                        chains.add(parts);
                    }
                }
            }
        }

        if (chains.isEmpty()) {
            throw new IOException("No food chains found in " + filename);
        }

        String[] chosen = chains.get(random.nextInt(chains.size()));
        // Order: prey,predator,apex,food
        return new String[]{chosen[2], chosen[1], chosen[0], chosen[3]};
    }
}
