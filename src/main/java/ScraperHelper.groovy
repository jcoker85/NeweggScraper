import groovyx.net.http.HTTPBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

static ArrayList<String> scrape(String uri, String maxPrice, boolean test) {
    def ret = new ArrayList<String>()

    if (!uri.contains("newegg")) {
        return ret
    }

    String AUTO_NOTIFY = "AUTO NOTIFY"
    String SOLD_OUT = "SOLD OUT"

    String price = "";
    int numPrice = 0;
    int numMaxPrice = 0;
    if (maxPrice.length() != 0) {
        maxPrice = maxPrice.replace(",", "").replace("\$", "")
        int priceIndex = maxPrice.indexOf(".")
        if (priceIndex != -1)
            maxPrice = maxPrice.substring(0, priceIndex)
        numMaxPrice = Integer.parseInt(maxPrice)
    }

    def http = new HTTPBuilder(uri.replace(" ", "%20"))

    def html = http.get([:])

    //Newegg
    html."**".findAll { it.@class.toString().contains("item-container") }.each {
        numPrice = 0
        String product = it.A[0].IMG[0].attributes().get("title").toString()
        String link = it.A[0].attributes().get("href").toString()
        it."**".find { it.@class.toString().contains("item-action") }.each {
            it."**".find { it.@class.toString().contains("price-current") }.each {
                price = it.STRONG[0].text()
                if (price.length() != 0) {
                    price = price.replace(",", "")
                    int priceIndex = price.indexOf(".")
                    if (priceIndex != -1)
                        price = price.substring(0, priceIndex)
                    if (numPrice == 0)
                        numPrice = Integer.parseInt(price)
                }
            }
            it."**".find { it.@class.toString().contains("item-operate") }.each {
                it."**".find { it.@class.toString().contains("item-button-area") }.each {
                    it."**".find { it.@class.toString().contains("btn") }.each {
                        if (test) {
                            if (it.text()?.toUpperCase()?.contains(AUTO_NOTIFY) || it.text()?.toUpperCase()?.contains(SOLD_OUT)) {
                                if (numPrice <= numMaxPrice)
                                    ret.add(product + "‽" + link)
                            }
                        } else {
                            if (!it.text()?.toUpperCase()?.contains(AUTO_NOTIFY) && !it.text()?.toUpperCase()?.contains(SOLD_OUT)) {
                                if (numPrice <= numMaxPrice)
                                    ret.add(product + "‽" + link)
                            }
                        }
                    }
                }
            }
        }
    }

    return ret
}

static ArrayList<String> scrapeIndividual(String uri, String maxPrice, boolean test) {
    def ret = new ArrayList<String>()

    if (!uri.contains("newegg")) {
        return ret
    }

    String AUTO_NOTIFY = "AUTO NOTIFY"
    String SOLD_OUT = "SOLD OUT"
    def http = new HTTPBuilder(uri.replace(" ", "%20"))

    def html = http.get([:])

    String product = "";
    String itemLink = "";
    String price = "";
    int numPrice = 0;
    int numMaxPrice = 0;
    if (maxPrice.length() != 0) {
        maxPrice = maxPrice.replace(",", "").replace("\$", "")
        int priceIndex = maxPrice.indexOf(".")
        if (priceIndex != -1)
            maxPrice = maxPrice.substring(0, priceIndex)
        numMaxPrice = Integer.parseInt(maxPrice)
    }

    //Newegg
    html."**".findAll { it.@class.toString().contains("item-container") }.each {
        numPrice = 0
        it."**".find { it.@class.toString().contains("item-info") }.each {
            product = it.A[0].text().toString()
            itemLink = it.A[0].attributes().get("href").toString()
        }
        def http2 = new HTTPBuilder(itemLink.replace(" ", "%20"))
        def html2 = http2.get([:])
        html2."**".find { it.@class.toString().contains("price-current") }.each {
            price = it.STRONG[0].text()
            if (price.length() != 0) {
                price = price.replace(",", "")
                int priceIndex = price.indexOf(".")
                if (priceIndex != -1)
                    price = price.substring(0, priceIndex)
                if (numPrice == 0)
                    numPrice = Integer.parseInt(price)
            }
        }
        html2."**".find { it.@class.toString().contains("grpPricing") }.each {
            it."**".find { it.@class.toString().contains("current") }.each {
                price = it.attributes().get("content").toString()
                if (price.length() != 0) {
                    price = price.replace(",", "")
                    int priceIndex = price.indexOf(".")
                    if (priceIndex != -1)
                        price = price.substring(0, priceIndex)
                    if (numPrice == 0)
                        numPrice = Integer.parseInt(price)
                }
            }
        }
        html2."**".find { it.@class.toString().contains("btn-wide") }.each {
            if (test) {
                if (it.text()?.toUpperCase()?.contains(AUTO_NOTIFY) || it.text()?.toUpperCase()?.contains(SOLD_OUT)) {
                    if (numPrice <= numMaxPrice)
                        ret.add(product + "‽" + itemLink)
                }
            } else {
                if (!it.text()?.toUpperCase()?.contains(AUTO_NOTIFY) && !it.text()?.toUpperCase()?.contains(SOLD_OUT)) {
                    if (numPrice <= numMaxPrice)
                        ret.add(product + "‽" + itemLink)
                }
            }
        }
        html2."**".find { it.@class.toString().contains("atnPrimary") }.each {
            if (test) {
                if (it.text()?.toUpperCase()?.contains(AUTO_NOTIFY) || it.text()?.toUpperCase()?.contains(SOLD_OUT)) {
                    if (numPrice <= numMaxPrice)
                        ret.add(product + "‽" + itemLink)
                }
            } else {
                if (!it.text()?.toUpperCase()?.contains(AUTO_NOTIFY) && !it.text()?.toUpperCase()?.contains(SOLD_OUT)) {
                    if (numPrice <= numMaxPrice)
                        ret.add(product + "‽" + itemLink)
                }
            }

        }
    }

    return ret
}


static ArrayList<String> scrapeBestBuy(String uri, boolean test) {

    String linkPrefix = "https://www.bestbuy.com"

    def ret = new ArrayList<String>()

    if (!uri.contains("bestbuy")) {
        return ret
    }

    String AUTO_NOTIFY = "AUTO NOTIFY"
    String SOLD_OUT = "SOLD OUT"
    String CHECK_STORES = "CHECK STORES"
    String SHOP_OPEN_BOX = "SHOP OPEN-BOX"
    String COMING_SOON = "COMING SOON"

    URL url = new URL(uri.replace(" ", "%20"))
    Document doc = Jsoup.parse(url, 5000)

    Elements iter = doc.select("div[class=right-column]")

    for (each in iter) {

        Elements productLink = each.select("h4[class=sku-header]")

        String link = linkPrefix + productLink.select("a").first().attributes().get("href")
        String product = productLink.select("a").first().text()

        Element itemButton = each.select("div[class=sku-list-item-button]").first()
        String buttonText = ""
        if (itemButton.select("button").first()) {
            buttonText = itemButton.select("button").first().text()
        } else {
            buttonText = itemButton.select("a").first().text()
        }

        if (test) {
            if (buttonText?.toUpperCase()?.contains(AUTO_NOTIFY) || buttonText?.toUpperCase()?.contains(SOLD_OUT) ||
                    buttonText?.toUpperCase()?.contains(CHECK_STORES) || buttonText?.toUpperCase()?.contains(SHOP_OPEN_BOX) ||
                    buttonText?.toUpperCase()?.contains(COMING_SOON)) {
                ret.add(product + "‽" + link)
            }
        } else {
            if (!buttonText?.toUpperCase()?.contains(AUTO_NOTIFY) && !buttonText?.toUpperCase()?.contains(SOLD_OUT) &&
                    !buttonText?.toUpperCase()?.contains(CHECK_STORES) && !buttonText?.toUpperCase()?.contains(SHOP_OPEN_BOX) &&
                    !buttonText?.toUpperCase()?.contains(COMING_SOON)) {
                ret.add(product + "‽" + link)
            }
        }
    }

    return ret
}