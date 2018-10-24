/**
 *
 */
package kr.co.kpcard.bots;

import javax.annotation.PostConstruct;

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

import kr.co.kpcard.bots.command.HelloCommand;
import kr.co.kpcard.bots.command.SwitchCommand;

/**
 * @author chris
 *
 */
@Component
public class CommandHandler extends TelegramLongPollingCommandBot {

	private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

	@Autowired
	private HelloCommand	helloCommand;

	@Autowired
	private SwitchCommand	switchCommand;

	@Value("${bot.command.token}")
	private String token;

	/**
	 * @param botUsername
	 */
	@Autowired
	public CommandHandler(@Value("${bot.command.username}") String botUsername) {
		super(botUsername);
		// TODO Auto-generated constructor stub
//		register(new SwitchCommand("Switch", "Execute switch command."));
	}

	@PostConstruct
    public void init(){
        register(helloCommand);
        register(switchCommand);
    }
	/* (non-Javadoc)
	 * @see org.telegram.telegrambots.bots.DefaultAbsSender#getBotToken()
	 */
	@Override
	public String getBotToken() {
		return token;
	}

	/* (non-Javadoc)
	 * @see org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot#processNonCommandUpdate(org.telegram.telegrambots.api.objects.Update)
	 */
	/**
	 * 이벤트 수신 시 동작
	 */
	@Override
	public void processNonCommandUpdate(Update update) {
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
