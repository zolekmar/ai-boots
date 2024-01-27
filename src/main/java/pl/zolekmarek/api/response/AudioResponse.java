package pl.zolekmarek.api.response;

public record AudioResponse(String text) {

    public static AudioResponse empty() {
        return new AudioResponse("");
    }
}
