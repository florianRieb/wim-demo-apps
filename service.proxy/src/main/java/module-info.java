import com.service.proxy.ProxyImpl;

module service.proxy {
    requires service.api;
    requires jeromq;

    provides com.api.CalcService with ProxyImpl;
}