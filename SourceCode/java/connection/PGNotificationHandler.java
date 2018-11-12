package connection;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


public class PGNotificationHandler implements PGNotificationListener {

    private PGConnection conn;
    private static PGNotificationHandler instance;

    public static PGNotificationHandler getInstance() {
        if(instance == null) {
            instance = new PGNotificationHandler();
        }

        return instance;
    }

    private PGNotificationHandler() {
        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost("hakurei.trashprojects.moe");
        dataSource.setPort(5432);
        dataSource.setDatabase("sep");
        dataSource.setUser("sep");
        dataSource.setPassword("ayy1mao");

        try {
            this.conn = (PGConnection) dataSource.getConnection();

            for(var channel : NotificationChannel.values()) {
                Statement stmt = conn.createStatement();
                stmt.execute(String.format("LISTEN %s", channel.name()));
                stmt.close();
            }

            conn.addNotificationListener(this);

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                conn.close();
            } catch(SQLException ex) {
                //whatever, we're quitting the application anyway
            }
        }));
    }

    @Override
    public void notification(int processId, String channelName, String payload) {
        for(var channel : NotificationChannel.values()) {
            if(channel.getChannelName().equalsIgnoreCase(channelName)) {
                for (var client : channel.getClients()) {
                    try {
                        client.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * adds a {@link Callable} to the list of items invoked when an event on the specified {@link NotificationChannel}
     * is fired
     * @param channel the channel you want to listen on
     * @param callable the callable to call when an event is fired
     */
    public void registerListener(NotificationChannel channel, Callable callable) {
        channel.registerListener(callable);
    }

    /**
     * removed a listener from a channel
     * @param channel the channel the listener should be removed from
     * @param callable the callable to remove
     */
    public void removeListener(NotificationChannel channel, Callable callable) {
        channel.removeListener(callable);
    }

    public enum NotificationChannel {
        //these have to correspond to the ones used in resources/sql/triggers.sql
        DATA("DATA"), CHAT("CHAT");

        private List<Callable> clients = new LinkedList<>();
        private String channelName;

        NotificationChannel(String channelName) {
            this.channelName = channelName;
        }

        protected String getChannelName() {
            return this.channelName;
        }

        protected void registerListener(Callable c) {
            clients.add(c);
        }

        protected void removeListener(Callable c) {
            clients.remove(c);
        }

        protected List<Callable> getClients() {
            return clients;
        }

        public String getTriggerFunctionSQL() {
            return String.format(
                    "CREATE OR REPLACE FUNCTION %s() RETURNS TRIGGER AS " +
                    "$$ " +
                    "BEGIN " +
                    "  EXECUTE 'NOTIFY %s'; " +
                    "  RETURN NEW; " +
                    "END; " +
                    "$$ LANGUAGE PLPGSQL;",
                    getProcedureName(),
                    getChannelName()
            );
        }

        public String getProcedureName() {
            return String.format("%s_notify", getChannelName());
        }
    }
}
