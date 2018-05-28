module service.local {
    requires service.api;
    requires java.logging;
    provides com.api.CalcService with com.service.LocalImpl;

}