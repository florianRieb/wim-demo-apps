#!/bin/bash

#Start Remote Service
java --module-path mlib -m service.remoteImpl/com.service.remote.RemoteService

#Start App
java --module-path mlib -m app/com.app.Main 1.0 2.0 3.0

