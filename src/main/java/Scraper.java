import org.apache.commons.lang3.StringEscapeUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {

    public boolean keepScraping = false;
    public boolean scrapeIndividual = false;
    public boolean muteSound = false;

    private List<String> data3080 = new ArrayList<>();
    private List<String> dataCpu = new ArrayList<>();
    private List<String> dataPS5 = new ArrayList<>();

    JFrame frame = new JFrame();
    private final UrlTextPane pane3080 = new UrlTextPane();
    private final UrlTextPane paneCpu = new UrlTextPane();
    private final UrlTextPane panePS5 = new UrlTextPane();

    private final JTextField urlNewegg3080 = new JTextField("https://www.newegg.com/p/pl?d=rtx+3080&N=100007709&isdeptsrh=1");
    private final JTextField urlNeweggCpu = new JTextField("https://www.newegg.com/p/pl?d=ryzen+5000&N=50001028");
    private final JTextField urlNeweggPS5 = new JTextField("https://www.newegg.com/p/pl?d=playstation+5&N=101696840&isdeptsrh=1");
    private final JTextField urlBestbuy3080 = new JTextField("https://www.bestbuy.com/site/searchpage.jsp?st=3080+rtx");
    private final JTextField urlBestbuyCpu = new JTextField("https://www.bestbuy.com/site/searchpage.jsp?qp=brand_facet%3DBrand~AMD&st=amd%20ryzen%205800%205900");
    private final JTextField urlBestbuyPS5 = new JTextField("https://www.bestbuy.com/site/playstation-5/ps5-consoles/pcmcat1587395025973.c?id=pcmcat1587395025973");

    private final JButton enableScrape = new JButton("In/Out Of Stock");
    private final JButton enableIndividual = new JButton("Individual Pages");
    private final JButton enableMute = new JButton("Mute Sound");

    public Scraper() {

        JPanel panel3080 = new JPanel();
        JPanel panelCpu = new JPanel();
        JPanel panelPs5 = new JPanel();

        Timer timer = new Timer(5000, e -> {
            pane3080.setText(convertText(data3080));
            paneCpu.setText(convertText(dataCpu));
            panePS5.setText(convertText(dataPS5));
        });
        timer.start();

        enableScrape.setBackground(Color.RED);
        enableScrape.addActionListener(arg0 -> {
            if (keepScraping) {
                keepScraping = false;
                enableScrape.setBackground(Color.RED);
            } else {
                keepScraping = true;
                enableScrape.setBackground(Color.GREEN);
            }
            setUrls(urlNewegg3080.getText(), urlNeweggCpu.getText(), urlNeweggPS5.getText());
        });

        enableIndividual.setBackground(Color.RED);
        enableIndividual.addActionListener(arg0 -> {
            if (scrapeIndividual) {
                scrapeIndividual = false;
                enableIndividual.setBackground(Color.RED);
            } else {
                scrapeIndividual = true;
                enableIndividual.setBackground(Color.GREEN);
            }
        });

        enableMute.setBackground(Color.RED);
        enableMute.addActionListener(arg0 -> {
            if (muteSound) {
                muteSound = false;
                enableMute.setBackground(Color.RED);
            } else {
                muteSound = true;
                enableMute.setBackground(Color.GREEN);
            }
        });

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new GridLayout(1, 3));
        mainContainer.add(new JScrollPane(setUpPanel(panel3080, pane3080, "RTX 3080 Tracker", "RTX 3080")));
        mainContainer.add(new JScrollPane(setUpPanel(panelCpu, paneCpu, "Ryzen 5000 Series Tracker", "Ryzen 5000 Series")));
        mainContainer.add(new JScrollPane(setUpPanel(panelPs5, panePS5, "Playstation 5 Tracker", "Playstation 5 Console")));
        mainContainer.setPreferredSize(new Dimension(1900, 860));

        JPanel urlContainer = new JPanel();
        urlContainer.setLayout(new GridLayout(1, 3));
        urlContainer.add(urlNewegg3080);
        urlContainer.add(urlNeweggCpu);
        urlContainer.add(urlNeweggPS5);
        urlContainer.setPreferredSize(new Dimension(1900, 30));

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(1, 3));
        buttonContainer.add(enableScrape);
        buttonContainer.add(enableIndividual);
        buttonContainer.add(enableMute);
        buttonContainer.setPreferredSize(new Dimension(1900, 60));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1920, 1020));
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.add(mainContainer, BorderLayout.NORTH);
        frame.add(urlContainer, BorderLayout.SOUTH);
        frame.add(buttonContainer, BorderLayout.SOUTH);
        frame.setTitle("NE and BB Tracker");
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Runner.closer();
                System.exit(0);
            }
        });
    }

    public void setNewegg3080Content(List<String> data) {
        data3080 = data;
    }

    public void setNeweggCpuContent(List<String> data) {
        dataCpu = data;
    }

    public void setNeweggPs5Content(List<String> data) {
        dataPS5 = data;
    }

    public void setUrls(String uri3080, String uriCpu, String uriPs5) {
        urlNewegg3080.setText(uri3080);
        urlNeweggCpu.setText(uriCpu);
        urlNeweggPS5.setText(uriPs5);
    }

    public List<String> getUrls() {
        List<String> ret = new ArrayList<>();
        ret.add(urlNewegg3080.getText());
        ret.add(urlNeweggCpu.getText());
        ret.add(urlNeweggPS5.getText());
        return ret;
    }

    private Component setUpPanel(JPanel panel, UrlTextPane urlTextPane, String title, String subtitle) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitleColor(Color.BLACK);

        panel.setBorder(titledBorder); //Set title to the Panel
        panel.setLayout(new BorderLayout());
        urlTextPane.setBorder(BorderFactory.createTitledBorder(subtitle));
        panel.add(urlTextPane, BorderLayout.NORTH);

        return panel;
    }

    private String convertText(List<String> data) {

        if (data.size() == 0) {
            return "";
        }

        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern urlPattern = Pattern.compile(regex);

        StringBuilder answer = new StringBuilder();
        answer.append("<html><body>");

        for (String entry : data) {
            String[] split = entry.split("‽");
            String content = StringEscapeUtils.escapeHtml4(split[1]).replace(" ", "%20");
            int lastIndex = 0;
            Matcher matcher = urlPattern.matcher(content);
            while (matcher.find()) {
                //Append everything since last update to the url:
                answer.append(content, lastIndex, matcher.start());
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
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException("Can't open URL", e);
                    }
                }
            }
        }
    }
}

