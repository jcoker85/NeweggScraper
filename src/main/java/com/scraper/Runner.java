package com.scraper;

import com.scraper.helper.ScraperHelper;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.lang.Thread.sleep;

public class Runner {

    private static FileHandler fh;
    private static BufferedInputStream bufferedInputStream;
    private static AudioInputStream audioInputStream;
    private static final Logger logger = Logger.getLogger("pooplog");

    public static void main(String[] args) {
        List<String> scrape1 = new ArrayList<>();
        List<String> scrape2 = new ArrayList<>();
        List<String> scrape3 = new ArrayList<>();

        try {
            fh = new FileHandler("poop.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            Scraper scraper = new Scraper();

            while (true) {
                List<String> urls = scraper.getUrls();
                if (scraper.keepScraping) {
                    try {
                        if (scraper.scrapeIndividual) {
                            scrape1 = ScraperHelper.scrapeIndividual(urls.get(0), false);
                            scrape2 = ScraperHelper.scrapeIndividual(urls.get(1), false);
                            scrape3 = ScraperHelper.scrapeIndividual(urls.get(2), false);
                        } else {
                            scrape1 = ScraperHelper.scrape(urls.get(0), false);
                            scrape2 = ScraperHelper.scrape(urls.get(1), false);
                            scrape3 = ScraperHelper.scrape(urls.get(2), false);
                        }
                    } catch (Exception e) {
                        logger.warning("Groovy Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
                    }
                    startAlert(scraper, scrape1, scrape2, scrape3);
                } else {
                    try {
                        if (scraper.scrapeIndividual) {
                            scrape1 = ScraperHelper.scrapeIndividual(urls.get(0), true);
                            scrape2 = ScraperHelper.scrapeIndividual(urls.get(1), true);
                            scrape3 = ScraperHelper.scrapeIndividual(urls.get(2), true);
                        } else {
                            scrape1 = ScraperHelper.scrape(urls.get(0), true);
                            scrape2 = ScraperHelper.scrape(urls.get(1), true);
                            scrape3 = ScraperHelper.scrape(urls.get(2), true);
                        }
                    } catch (Exception e) {
                        logger.warning("Groovy Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
                    }
                }
                scraper.setNewegg3080Content(scrape1);
                scraper.setNeweggCpuContent(scrape2);
                scraper.setNeweggPs5Content(scrape3);
                sleep(5000);
            }
        } catch (Exception e) {
            logger.warning("Main Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
        } finally {
            closer();
        }
    }

    private static void startAlert(Scraper scraper, List<String>... scrapeResult) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        for (List<String> result : scrapeResult) {
            for (String item : result) {
                logger.info(item.split("‽")[0] + " IN STOCK\r\n  " + item.split("‽")[1]);
            }
            if (!scraper.muteSound) {
                bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(Runner.class.getClassLoader().getResourceAsStream("alert.wav")));
                audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        }
    }

    public static void closer() {
        try {
            fh.close();
            if (bufferedInputStream != null)
                bufferedInputStream.close();
            if (audioInputStream != null)
                audioInputStream.close();
        } catch (IOException e) {
            // Do nothing
        }
    }
}
