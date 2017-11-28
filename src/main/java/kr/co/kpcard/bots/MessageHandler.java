/**
 * 
 */
package kr.co.kpcard.bots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author chris
 *
 */
@Component
public class MessageHandler extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
	
	@Value("${bot.message.token}")
	private String token;
	
	@Value("${bot.message.username}")
	private String username;
	
	/* (non-Javadoc)
	 * @see org.telegram.telegrambots.generics.LongPollingBot#getBotUsername()
	 */
	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	/* (non-Javadoc)
	 * @see org.telegram.telegrambots.bots.DefaultAbsSender#getBotToken()
	 */
	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return token;
	}

	/* (non-Javadoc)
	 * @see org.telegram.telegrambots.generics.LongPollingBot#onUpdateReceived(org.telegram.telegrambots.api.objects.Update)
	 */
	@Override
	public void onUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		Integer		id;
		String		text;
		String		message;
		
		logger.debug(update.toString());
		message = String.format("Message from id = %d, firstName = %s, lastName = %s", 
				update.getMessage().getFrom().getId(),
				update.getMessage().getFrom().getFirstName(),
				update.getMessage().getFrom().getLastName());
		logger.info(message);
		logger.debug(String.format("text = %s", update.getMessage().getText()));
		id = update.getMessage().getFrom().getId();
		text = update.getMessage().getText();
		
		SendMessage	sendMessage = new SendMessage();
//		sendMessage.setChatId("@KPCChannel");
		sendMessage.setChatId(id.toString());
		if ( "/who".equals(text) == true )
			sendMessage.setText(String.format("Your id is %s", id));
		else
			sendMessage.setText(text);
				
		try {
			Message recvNessage = execute(sendMessage);
			logger.debug(recvNessage.toString());
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
		}
	}

}
