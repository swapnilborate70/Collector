import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxCollector {

    public static void main(String[] args) {
        String host = "";
        String username = "";
        String password = "";
        int port = ;

        try
        {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            // Command to get network adapters information for linux OS
            String command = "ifconfig";

            //channel for executing command on remote server.
            //openChannel for opening new channel on session
            //'exce' means channel is opening for executing command.
            Channel channel = session.openChannel("exec");

            //channelExec is subclass of Channel.
            ((ChannelExec) channel).setCommand(command);

            // this set null if input command does not require any user input.
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            //establish conn with server and InputStream read output of command.
            channel.connect();
            InputStream in = channel.getInputStream();

            // Read the output of the command
            byte[] tmp = new byte[1024];
            StringBuilder output = new StringBuilder();
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output.append(new String(tmp, 0, i));
                }
                if (channel.isClosed())
                {
                    if (in.available() > 0)
                        continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    //this means that command execution success and completed.
                    break;
                }

            }

            // Close channel and session
            channel.disconnect();
            session.disconnect();

            String ifConfigOutput = output.toString();

            //pass the output to extract interface names and addresses
            parseIfConfigOutput(ifConfigOutput);

        }
        catch (JSchException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void parseIfConfigOutput(String ifConfigOutput) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+):.*?inet ([0-9.]+).*?inet6 ([a-fA-F0-9:]+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(ifConfigOutput);

        while (matcher.find())
        {

            String interfaceName = matcher.group(1);

            // Replace numeric interface names with "lo"
            // here it is printing '52' instead of 'lo' so it will change '52' to 'lo'
            if (interfaceName.matches("\\d+"))
            {
                interfaceName = "lo";
            }

            String ipv4Address = matcher.group(2);
            String ipv6Address = matcher.group(3);

            System.out.println("Interface Name: " + interfaceName);
            System.out.println("IPv4: " + ipv4Address);
            System.out.println("IPv6: " + ipv6Address);
            System.out.println();
        }
    }

}
