package com.brandon.globaleconomy.config;

import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class NameLoader {
    private final Map<String, List<String>> namesByNation = new HashMap<>();

    // Pass the data folder when you construct NameLoader: new NameLoader(getDataFolder())
    public NameLoader(File dataFolder) {
        // Always combine folder and file name!
        File file = new File(dataFolder, "names.yml");
        System.out.println("DEBUG: NameLoader looking for " + file.getAbsolutePath());
        loadNames(file);
    }

    @SuppressWarnings("unchecked")
    private void loadNames(File file) {
        try (InputStream is = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Object namesRoot = yaml.load(is);

            // Debugging line, remove if you want:
            System.out.println("DEBUG: namesRoot = " + namesRoot);

            if (namesRoot instanceof Map<?, ?>) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) namesRoot).entrySet()) {
                    String nation = entry.getKey().toString();
                    List<String> names = (List<String>) entry.getValue();
                    namesByNation.put(nation, names);
                }
            } else {
                System.err.println("ERROR: names.yml did not load as a map!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getNames(String nation) {
        return namesByNation.getOrDefault(nation, Collections.emptyList());
    }

    public Set<String> getNations() {
        return namesByNation.keySet();
    }

    public String getRandomName(String nation, Set<String> usedNames) {
        List<String> names = getNames(nation);
        if (names == null || names.isEmpty()) return "Worker";
        List<String> unused = new ArrayList<>(names);
        unused.removeAll(usedNames);
        if (unused.isEmpty()) return names.get(new Random().nextInt(names.size()));
        return unused.get(new Random().nextInt(unused.size()));
    }

}
