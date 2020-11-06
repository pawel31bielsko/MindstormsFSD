import time
import struct
import usb.util

from flask import Flask

import ev3_dc as ev3

app = Flask(__name__)


@app.route('/distance')
def get_current_distance():
    my_ev3 = ev3.EV3(protocol=ev3.USB, host='00:16:53:7f:79:65')
    my_ev3.verbosity = 1

    with my_ev3:

        # infrared sensor at port 3
        ops = b''.join((
            ev3.opInput_Device,
            ev3.READY_SI,
            ev3.LCX(0),  # LAYER
            ev3.PORT_4,  # NO
            ev3.LCX(33),  # TYPE - EV3-IR
            ev3.LCX(0),  # MODE - Proximity
            ev3.LCX(1),  # VALUES
            ev3.GVX(0)  # VALUE1
        ))
        reply = my_ev3.send_direct_cmd(ops, global_mem=4)
        distance = struct.unpack('<f', reply)[0]

        return {'distance': distance}


@app.route('/devices')
def get_connected_devices():
    return map(lambda d: {
        "manufacturer":  usb.util.get_string(d, 128, d.iManufacturer),
        "producer": usb.util.get_string(d, 128, d.iProduct),
        "serial_number":usb.util.get_string(d, d.iSerialNumber)
    }, usb.core.find(find_all=True))
