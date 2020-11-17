import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {

    public boolean keepScrapin = false;
    public boolean scrapeIndividual = false;
    public boolean muteSound = false;
    public boolean bestBuyMode = false;

    private ArrayList<String> data3080 = new ArrayList<String>();
    private ArrayList<String> data3090 = new ArrayList<String>();

    private UrlTextPane pane3080 = new UrlTextPane();
    private UrlTextPane pane3090 = new UrlTextPane();

    //private JTextField url3080 = new JTextField("https://www.bestbuy.com/site/searchpage.jsp?st=3080+rtx");
    private JTextField url3080 = new JTextField("https://www.newegg.com/p/pl?d=rtx+3080&N=100007709&isdeptsrh=1");
    //private JTextField url3090 = new JTextField("https://www.bestbuy.com/site/searchpage.jsp?st=3090+rtx");
    private JTextField url3090 = new JTextField("https://www.newegg.com/p/pl?d=rtx+3090&N=100007709&isdeptsrh=1");

    private JTextField price3080 = new JTextField("900");
    private JTextField price3090 = new JTextField("1800");

    private JButton enableScrape = new JButton("In/Out Of Stock");
    private JButton enableBestBuyMode = new JButton("Best Buy Mode");
    private JButton enableIndividual = new JButton("Individual Pages");
    private JButton enableMute = new JButton("Mute Sound");

    JFrame frame = new JFrame();

    public Scraper() {

        JPanel panel3080 = new JPanel();
        JPanel panel3090 = new JPanel();

        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pane3080.setText(convertText(data3080));
                pane3090.setText(convertText(data3090));
            }
        });
        timer.start();

        enableScrape.setBackground(Color.RED);
        enableScrape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (keepScrapin) {
                    keepScrapin = false;
                    enableScrape.setBackground(Color.RED);
                } else {
                    keepScrapin = true;
                    enableScrape.setBackground(Color.GREEN);
                }
                setUrls(url3080.getText(), url3090.getText());
                setPrices(price3080.getText(), price3090.getText());
            }
        });

        enableBestBuyMode.setBackground(Color.RED);
        enableBestBuyMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (bestBuyMode) {
                    bestBuyMode = false;
                    enableBestBuyMode.setBackground(Color.RED);
                } else {
                    bestBuyMode = true;
                    enableBestBuyMode.setBackground(Color.GREEN);
                }
            }
        });

        enableIndividual.setBackground(Color.RED);
        enableIndividual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (scrapeIndividual) {
                    scrapeIndividual = false;
                    enableIndividual.setBackground(Color.RED);
                } else {
                    scrapeIndividual = true;
                    enableIndividual.setBackground(Color.GREEN);
                }
            }
        });

        enableMute.setBackground(Color.RED);
        enableMute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (muteSound) {
                    muteSound = false;
                    enableMute.setBackground(Color.RED);
                } else {
                    muteSound = true;
                    enableMute.setBackground(Color.GREEN);
                }
            }
        });

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new GridLayout(1, 2));
        mainContainer.add(setup3080Panel(panel3080, pane3080));
        mainContainer.add(setup3090Panel(panel3090, pane3090));
        mainContainer.setPreferredSize(new Dimension(1175, 660));
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(4, 2));
        buttonContainer.add(url3080);
        buttonContainer.add(url3090);
        buttonContainer.add(price3080);
        buttonContainer.add(price3090);
        buttonContainer.add(enableScrape);
        buttonContainer.add(enableBestBuyMode);
        buttonContainer.add(enableIndividual);
        buttonContainer.add(enableMute);
        buttonContainer.setPreferredSize(new Dimension(1175, 160));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 875));
        frame.setLayout(new FlowLayout(0));
        frame.add(mainContainer, BorderLayout.NORTH);
        frame.add(buttonContainer, BorderLayout.SOUTH);
        frame.setTitle("TRACKERBOI 9000");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Runner.closer();
                System.exit(0);
            }
        });
    }

    public void set3080Content(ArrayList<String> data) {
        data3080 = data;
    }

    public void set3090Content(ArrayList<String> data) {
        data3090 = data;
    }

    public void setUrls(String uri3080, String uri3090) {
        url3080.setText(uri3080);
        url3090.setText(uri3090);
    }

    public void setPrices(String prc3080, String prc3090) {
        price3080.setText(prc3080);
        price3090.setText(prc3090);
    }

    public ArrayList<String> getUrls() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(url3080.getText());
        ret.add(url3090.getText());
        return ret;
    }

    public ArrayList<String> getPrices() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(price3080.getText());
        ret.add(price3090.getText());
        return ret;
    }

    private Component setup3080Panel(JPanel panel3080, UrlTextPane pane3080) {

        //Setup 3080 panel
        TitledBorder title = BorderFactory.createTitledBorder("3080 Tracker");
        title.setTitleJustification(TitledBorder.CENTER);
        title.setTitleColor(Color.BLACK);
        panel3080.setBorder(title); //Set title to the Panel

        panel3080.setLayout(new BorderLayout());

        pane3080.setBorder(BorderFactory.createTitledBorder("3080"));

        panel3080.add(pane3080, BorderLayout.NORTH);

        return panel3080;
    }

    private Component setup3090Panel(JPanel panel3090, UrlTextPane pane3090) {

        //Setup 3090 panel
        TitledBorder title = BorderFactory.createTitledBorder("3090 Tracker");
        title.setTitleJustification(TitledBorder.CENTER);
        title.setTitleColor(Color.BLACK);
        panel3090.setBorder(title); //Set title to the Panel

        panel3090.setLayout(new BorderLayout());

        pane3090.setBorder(BorderFactory.createTitledBorder("3090"));

        panel3090.add(pane3090, BorderLayout.NORTH);

        return panel3090;
    }

    private String convertText(ArrayList<String> data) {

        if (data.size() == 0) {
            return "";
        }

        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern urlPattern = Pattern.compile(regex);

        StringBuilder answer = new StringBuilder();
        answer.append("<html><body>");

        for (String entry : data) {
            String split[] = entry.split("‽");
            String content = StringEscapeUtils.escapeHtml4(split[1]).replace(" ", "%20");
            int lastIndex = 0;
            Matcher matcher = urlPattern.matcher(content);
            while (matcher.find()) {
                //Append everything since last update to the url:
                answer.append(content.substring(lastIndex, matcher.start()));
                String url = content.substring(matcher.start(), matcher.end()).trim();
                answer.append("<a href=\"" + url + "\">" + split[0] + "</a>");
                lastIndex = matcher.end();
            }
            answer.append(content.substring(lastIndex));
            answer.append("<br><br>");
        }
        //Append end:
        return answer.toString();
    }

    private static class UrlTextPane extends JTextPane {

        public UrlTextPane() {
            this.setEditable(false);
            this.addHyperlinkListener(new UrlHyperlinkListener());
            this.setContentType("text/html");
        }

        private static class UrlHyperlinkListener implements HyperlinkListener {
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    } catch (IOException e) {
                        throw new RuntimeException("Can't open URL", e);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException("Can't open URL", e);
                    }
                }
            }
        }
    }
}

