#!/usr/bin/env python3

import ev3_dc as ev3

my_ev3 = ev3.EV3(protocol=ev3.USB, host='00:16:53:7f:79:65')
my_ev3.verbosity = 1
ops = b''.join((
    ev3.opSound,
    ev3.TONE,
    ev3.LCX(1),  # VOLUME
    ev3.LCX(440),  # FREQUENCY
    ev3.LCX(1000),  # DURATION
))
my_ev3.send_direct_cmd(ops)