package com.socialnetwork;

import com.socialnetwork.command.CommandHandler;
import com.socialnetwork.model.Clock;
import com.socialnetwork.parser.CommandParser;
import com.socialnetwork.repository.InMemoryFollowRepository;
import com.socialnetwork.repository.InMemoryMessageRepository;
import com.socialnetwork.service.SocialNetworkService;
import com.socialnetwork.service.TimeFormatter;

import java.util.Scanner;

public class SocialNetworkApp {

    public static void main(String[] args) {
        var clock           = Clock.system();
        var messageRepo     = new InMemoryMessageRepository();
        var followRepo      = new InMemoryFollowRepository();
        var timeFormatter   = new TimeFormatter(clock);
        var service         = new SocialNetworkService(messageRepo, followRepo, timeFormatter, clock);
        var handler         = new CommandHandler(service, System.out);
        var parser          = new CommandParser();

        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parser.parse(line).ifPresent(handler::handle);
            }
        }
    }
}
