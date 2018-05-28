import com.service.local.LocalImpl;

module service.local {
    requires service.api;
    requires java.logging;
    provides com.api.CalcService with LocalImpl;

}