package xander479.musicbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.audio.AudioConnection;

public class MusicBot {
	private static MusicBot instance;
	private final String token;
	private DiscordApi api;
	private BotProperties properties;

	public static void main(String[] args) {
		getInstance();
	}
	
	private MusicBot() {
		token = "Your Discord's Bot Token Here";
		api = new DiscordApiBuilder().setToken(token).login().join();
		api.addMessageCreateListener(new DiscordMessageListener());
		try {
			properties = BotProperties.getProperties();
		}
		catch(FileNotFoundException e) {
			// Expected if using for the first time
			properties = BotProperties.initialise();
		}
		catch(ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		// Set properties now, so the constructors don't enter an infinite loop
		((DiscordMessageListener)api.getMessageCreateListeners().get(0)).setProperties(properties);
	}
	
	public static synchronized MusicBot getInstance() {
		if(instance == null) instance = new MusicBot();
		return instance;
	}
	
	public BotProperties getProperties() {
		return properties;
	}
	
	public DiscordApi getApi() {
		return api;
	}
	
	public void disconnect() {
		api.disconnect();
	}
	
	static class BotProperties implements Serializable {
		private static final long serialVersionUID = 1L;
		private String prefix = "!";
		private AudioConnection auCon;
		
		private BotProperties() {
			prefix = "!";
			try {
				saveProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private static BotProperties initialise() {
			return new BotProperties();
		}
		
		private static BotProperties getProperties() throws FileNotFoundException, IOException, ClassNotFoundException {
			try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("bot.properties"))) {
				Object properties = in.readObject();
				if(properties instanceof BotProperties) return (BotProperties)properties;
			}
			throw new IOException("Unable to deserialise properties");
		}
		
		public void saveProperties() throws FileNotFoundException, IOException {
			try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("bot.properties"))) {
				out.writeObject(this);
			}
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		public AudioConnection getAudioConnection() {
			return auCon;
		}
		
		public void setAudioConnection(AudioConnection auCon) {
			this.auCon = auCon;
		}
	}
}
