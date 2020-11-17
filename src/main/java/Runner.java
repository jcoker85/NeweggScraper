import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import static java.lang.Thread.sleep;

public class Runner {

    private static FileHandler fh;
    private static BufferedInputStream bufferedInputStream;
    private static AudioInputStream audioInputStream;

    public static void main(String[] args) {

        Logger logger = Logger.getLogger("pooplog");

        try {
            fh = new FileHandler("poop.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            Scraper scraper = new Scraper();


            while (true) {
                ArrayList<String> urls = scraper.getUrls();
                ArrayList<String> prices = scraper.getPrices();
                if (scraper.keepScrapin) {
                    if (urls.size() > 0) {
                        ArrayList<String> scrape1 = new ArrayList<String>();
                        try {
                            if (scraper.bestBuyMode) {
                                scrape1 = ScraperHelper.scrapeBestBuy(urls.get(0), false);
                            } else {
                                if (scraper.scrapeIndividual)
                                    scrape1 = ScraperHelper.scrapeIndividual(urls.get(0), prices.get(0), false);
                                else
                                    scrape1 = ScraperHelper.scrape(urls.get(0), prices.get(0),false);
                            }
                        } catch (Exception e) {
                            logger.warning("Groovy Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
                        }
                        if (scrape1.size() > 0) {
                            for (String item : scrape1) {
                                logger.info(item.split("‽")[0] + " IN STOCK\r\n      " + item.split("‽")[1]);
                            }
                            if (!scraper.muteSound) {
                                bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(Runner.class.getClassLoader().getResourceAsStream("alert.wav")));
                                audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
                                Clip clip = AudioSystem.getClip();
                                clip.open(audioInputStream);
                                clip.start();
                            }
                        }
                        scraper.set3080Content(scrape1);
                    }

                    if (urls.size() > 1) {
                        ArrayList<String> scrape2 = new ArrayList<String>();
                        try {
                            if (scraper.bestBuyMode) {
                                scrape2 = ScraperHelper.scrapeBestBuy(urls.get(1), false);
                            } else {
                                if (scraper.scrapeIndividual)
                                    scrape2 = ScraperHelper.scrapeIndividual(urls.get(1), prices.get(1),false);
                                else
                                    scrape2 = ScraperHelper.scrape(urls.get(1), prices.get(1),false);
                            }
                        } catch (Exception e) {
                            logger.warning("Groovy Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
                        }
                        if (scrape2.size() > 0) {
                            for (String item : scrape2) {
                                logger.info(item.split("‽")[0] + " IN STOCK\r\n      " + item.split("‽")[1]);
                            }
                            if (!scraper.muteSound) {
                                bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(Runner.class.getClassLoader().getResourceAsStream("alert.wav")));
                                audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
                                Clip clip = AudioSystem.getClip();
                                clip.open(audioInputStream);
                                clip.start();
                            }
                        }
                        scraper.set3090Content(scrape2);
                    }
                } else {
                    if (urls.size() > 0) {
                        ArrayList<String> scrape1 = new ArrayList<String>();
                        try {
                            if (scraper.bestBuyMode) {
                                scrape1 = ScraperHelper.scrapeBestBuy(urls.get(0), true);
                            } else {
                                if (scraper.scrapeIndividual)
                                    scrape1 = ScraperHelper.scrapeIndividual(urls.get(0), prices.get(0),true);
                                else
                                    scrape1 = ScraperHelper.scrape(urls.get(0), prices.get(0),true);
                            }
                        } catch (Exception e) {
                            logger.warning("Groovy Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
                        }
                        scraper.set3080Content(scrape1);
                    }

                    if (urls.size() > 1) {
                        ArrayList<String> scrape2 = new ArrayList<String>();
                        try {
                            if (scraper.bestBuyMode) {
                                scrape2 = ScraperHelper.scrapeBestBuy(urls.get(1), true);
                            } else {
                                if (scraper.scrapeIndividual)
                                    scrape2 = ScraperHelper.scrapeIndividual(urls.get(1), prices.get(1),true);
                                else
                                    scrape2 = ScraperHelper.scrape(urls.get(1), prices.get(1),true);
                            }
                        } catch (Exception e) {
                            logger.warning("Groovy Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
                        }
                        scraper.set3090Content(scrape2);
                    }
                }
                sleep(5000);
            }
        } catch (Exception e) {
            logger.warning("Main Error detected: " + e.toString() + "\r\n         Stacktrace: " + Arrays.toString(e.getStackTrace()));
        } finally {
            closer();
        }
    }

    public static void closer()  {
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
