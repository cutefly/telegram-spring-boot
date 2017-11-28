/**
 * 
 */
package kr.co.kpcard.bots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author chris
 *
 */
@Component
public class CommandHandler extends TelegramLongPollingCommandBot {

	private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
	
	@Value("${bot.command.token}")
	private String token;
	
	/**
	 * @param botUsername
	 */
	@Autowired
	public CommandHandler(@Value("${bot.command.username}") String botUsername) {
		super(botUsername);
		// TODO Auto-generated constructor stub
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
	 * @see org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot#processNonCommandUpdate(org.telegram.telegrambots.api.objects.Update)
	 */
	@Override
	public void processNonCommandUpdate(Update update) {
		// TODO Auto-generated method stub
		if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(message.getChatId().toString());
                echoMessage.setText("Hey heres your message:\n" + message.getText());

                try {
                    execute(echoMessage);
                } catch (TelegramApiException e) {
                	logger.error(e.getMessage());
                }
            }
}
	}


}
