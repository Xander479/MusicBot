package xander479.musicbot;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class DiscordMessageListener implements MessageCreateListener {
	private MusicBot.BotProperties properties;
	
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		String prefix = properties.getPrefix();
		String msg = event.getMessageContent();
		if(!msg.startsWith(prefix)) return;
		
		String[] words = msg.split(" ");
		ServerTextChannel channel = event.getServerTextChannel().get();
		MessageAuthor author = event.getMessageAuthor();
		Server server = event.getServer().get();
		
		// checking command name
		switch(words[0].substring(prefix.length())) {
			case "join":
				channel.sendMessage("Joining `" + channel.getName() + "`");
				Optional<ServerVoiceChannel> vc = author.getConnectedVoiceChannel();
				if(vc.isPresent()) {
					try {
						properties.setAudioConnection(vc.get().connect().get());
					}
					catch (InterruptedException | ExecutionException e) {
						channel.sendMessage("Couldn't join voice channel");
						e.printStackTrace();
					}
				}
				else channel.sendMessage("You must be connected to a voice channel to use this command.");
				break;
				
			case "leave":
				vc = MusicBot.getInstance().getApi().getYourself().getConnectedVoiceChannel(server);
				properties.getAudioConnection().close();
				properties.setAudioConnection(null);
				break;
			
			case "disconnect":
				MusicBot.getInstance().disconnect();
		}
	}
	
	public void setProperties(MusicBot.BotProperties properties) {
		this.properties = properties;
	}
}
