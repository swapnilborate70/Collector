import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxCollector {

    public static void main(String[] args) {
        String host = "134.119.179.22";
        String username = "zabbix";
        String password = "RevDau@123";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 8822);
            session.setPassword(password);

            // Avoid asking for key confirmation
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            // Command to get network adapters information
            String command = "ifconfig";

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            // Read the output of the command
            byte[] tmp = new byte[1024];
            StringBuilder output = new StringBuilder();
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            // Close channel and session
            channel.disconnect();
            session.disconnect();

            // Parse the output to extract interface names and addresses
            String ifconfigOutput = output.toString();
            parseIfconfigOutput(ifconfigOutput);

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseIfconfigOutput(String ifconfigOutput) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+):.*?inet ([0-9.]+).*?inet6 ([a-fA-F0-9:]+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(ifconfigOutput);

        while (matcher.find()) {
            String adapterName = matcher.group(1);
            // Replace numeric interface names with "lo"
            if (adapterName.matches("\\d+")) {
                adapterName = "lo";
            }
            String ipv4Address = matcher.group(2);
            String ipv6Address = matcher.group(3);

            System.out.println("Interface Name: " + adapterName);
            System.out.println("IPv4: " + ipv4Address);
            System.out.println("IPv6: " + ipv6Address);
            System.out.println();
        }
    }

}
