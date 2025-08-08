import java.io.*;
import java.util.*;

public class MacroProcessor {
    static class MacroData {
        String name;
        int mdtIndex;
        List<String> parameters = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter mntWriter = new BufferedWriter(new FileWriter("mnt.txt"));
        BufferedWriter mdtWriter = new BufferedWriter(new FileWriter("mdt.txt"));
        BufferedWriter alaWriter = new BufferedWriter(new FileWriter("ala.txt"));

        List<String> mdt = new ArrayList<>();
        int mdtIndex = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equalsIgnoreCase("MACRO")) {
                String header = reader.readLine().trim(); // e.g., incr &n1,&n2,&dreg
                String[] parts = header.split("\\s+");
                String macroName = parts[0];
                String[] argsList = parts[1].split(",");

                MacroData macro = new MacroData();
                macro.name = macroName;
                macro.mdtIndex = mdtIndex;

                Map<String, String> alaMap = new LinkedHashMap<>();
                int paramNum = 1;

                for (String arg : argsList) {
                    String cleanArg = arg.trim();
                    macro.parameters.add(cleanArg);
                    alaMap.put(cleanArg, "#" + paramNum++);
                }

                // Write macro name line to MDT using original parameters
                mdt.add(header);
                mdtIndex++;

                while (!(line = reader.readLine().trim()).equalsIgnoreCase("MEND")) {
                    String processed = line;
                    for (Map.Entry<String, String> entry : alaMap.entrySet()) {
                        processed = processed.replace(entry.getKey(), entry.getValue());
                    }
                    mdt.add(processed);
                    mdtIndex++;
                }
                mdt.add("MEND");
                mdtIndex++;

                // Write to MNT
                mntWriter.write(macro.name + " " + macro.mdtIndex + "\n");

                // Write to ALA
                alaWriter.write("ALA for " + macro.name + ": " + String.join(", ", macro.parameters) + "\n");
            }
        }

        // Write MDT to file
        for (String mdtEntry : mdt) {
            mdtWriter.write(mdtEntry + "\n");
        }

        reader.close();
        mntWriter.close();
        mdtWriter.close();
        alaWriter.close();

        System.out.println("Macro processing complete. Files generated: mnt.txt, mdt.txt, ala.txt");
    }
}
