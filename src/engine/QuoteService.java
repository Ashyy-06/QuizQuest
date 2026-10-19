package engine;

public class QuoteService
{
    private String[] quotes =
    {
        "Believe in yourself and keep learning!",
        "Every mistake is a step towards improvement.",
        "Small progress is still progress.",
        "Practice makes you better every day.",
        "Your effort today builds your success tomorrow."
    };

    public String getRandomQuote()
    {
        int index = (int)(Math.random() * quotes.length);

        return quotes[index];
    }

    public void displayQuote()
    {
        System.out.println("\n" + getRandomQuote());
    }
}