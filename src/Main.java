import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args)
    {
        try
        {
            // Command to execute PowerShell script
            // and get output in String format
            String command = "powershell.exe -ExecutionPolicy Bypass C:\\Users\\swapn\\OneDrive\\Desktop\\Collector\\src\\script.ps1";

            while (true)
            {
                // Execute PowerShell script
                Process powerShellProcess = Runtime.getRuntime().exec(command);

                //this InputReader converts byte streams into characters.
                BufferedReader reader = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
                String line;

                //reader.readLine this will read the line until the line break
                //reader pointer stops to current line break point.
                while ((line = reader.readLine()) != null)
                {
                    System.out.println(line);
                }

                System.out.println(" ");
                System.out.println("Adapters data printed successfully.....");
                System.out.println(" ");
                System.out.println(" ");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
