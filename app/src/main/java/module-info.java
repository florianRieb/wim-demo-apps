module app {
    requires java.logging;

    requires customloghandler;
    requires service.api;

    uses com.api.CalcService;
}