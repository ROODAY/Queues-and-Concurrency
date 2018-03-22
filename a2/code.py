import numpy as np
import scipy as sp
import scipy.stats
import random
import queue
import collections
import math
import pprint

# birth = 1, death = 2, monitor = 3

def mean_confidence_interval(data, confidence=0.95):
    a = 1.0*np.array(data)
    n = len(a)
    m, se = np.mean(a), scipy.stats.sem(a)
    h = se * sp.stats.t._ppf((1+confidence)/2., n-1)
    return m, h

def waitingTime(lam):
    return math.log(1-random.random())/(-1 / lam)

def runSimulation(lam, ts, end, seed):
    time = 0
    system = 0
    processNum = 0
    random.seed(seed)
    schedule = []
    state = queue.Queue()
    schedule.append((waitingTime(1/lam), 1))
    schedule.append((waitingTime(lam) + end/2, 3))
    stats = []
    
    while(time < end):
        curEvent = schedule.pop(0)
        time = curEvent[0]
        eventType = curEvent[1]
        
        if (eventType == 1):
            state.put(processNum)
            processNum += 1
            if (state.qsize() == 1 and system == 0):
                schedule.append((time + waitingTime(ts), 2))
                system = 1

            schedule.append((time + waitingTime(1/lam), 1))
        elif (eventType == 2):
            death = state.get()
            if (state.qsize() > 0):
                schedule.append((time + waitingTime(ts), 2))
                system = 1
            else:
                system = 0
            
        elif (eventType == 3 and time >= end/2):
            w = state.qsize()
            q = w+(waitingTime(ts)*lam)
            Tw = w/lam
            Tq = ts +Tw
            util = lam/(1/waitingTime(ts))
            stats.append({
                "w": w,
                "q": q,
                "Tw": Tw,
                "Tq": Tq,
                "util": util
            })
            
            schedule.append((time + waitingTime(lam), 3))
            
        schedule.sort(key=lambda tup: tup[0])
        
    finalStats = ({
        "w": np.mean([item['w'] for item in stats]),
        "q": np.mean([item['q'] for item in stats]),
        "Tw": np.mean([item['Tw'] for item in stats]),
        "Tq": np.mean([item['Tq'] for item in stats]),
        "util": np.mean([item['util'] for item in stats])
    }, {
        "w": mean_confidence_interval([item['w'] for item in stats]),
        "q": mean_confidence_interval([item['q'] for item in stats]),
        "Tw": mean_confidence_interval([item['Tw'] for item in stats]),
        "Tq": mean_confidence_interval([item['Tq'] for item in stats]),
        "util": mean_confidence_interval([item['util'] for item in stats])
    })
    return finalStats

pp = pprint.PrettyPrinter(indent=4)
trials = []

trials.append(runSimulation(6, 0.15, 2000, 4522425020))
trials.append(runSimulation(6, 0.15, 2000, 8448481091))
trials.append(runSimulation(6, 0.15, 2000, 1834903635))
trials.append(runSimulation(6, 0.15, 2000, 4063369934))
trials.append(runSimulation(6, 0.15, 2000, 5987411937))
print("Ts = 0.15")
print("Calculated:")
pp.pprint({
    "w": 8.1,
    "q": 9,
    "Tw": 1.35,
    "Tq": 1.5,
    "util": .9
})
print("Simulated:")
pp.pprint({
    "w": "{:.2f}".format(np.mean([item[0]['w'] for item in trials])),
    "q": "{:.2f}".format(np.mean([item[0]['q'] for item in trials])),
    "Tw": "{:.2f}".format(np.mean([item[0]['Tw'] for item in trials])),
    "Tq": "{:.2f}".format(np.mean([item[0]['Tq'] for item in trials])),
    "util": "{:.2f}".format(np.mean([item[0]['util'] for item in trials]))
})
print("Confidence Intervals:")
pp.pprint({
    "w": "{:.2f} +- {:.2f}".format(trials[0][1]['w'][0], trials[0][1]['w'][1]),
    "q": "{:.2f} +- {:.2f}".format(trials[0][1]['q'][0], trials[0][1]['q'][1]),
    "Tw": "{:.2f} +- {:.2f}".format(trials[0][1]['Tw'][0], trials[0][1]['Tw'][1]),
    "Tq": "{:.2f} +- {:.2f}".format(trials[0][1]['Tq'][0], trials[0][1]['Tq'][1]),
    "util": "{:.2f} +- {:.2f}".format(trials[0][1]['util'][0], trials[0][1]['util'][1])
})

trials = []
trials.append(runSimulation(6, 0.12, 2000, 4522425020))
trials.append(runSimulation(6, 0.12, 2000, 8448481091))
trials.append(runSimulation(6, 0.12, 2000, 1834903635))
trials.append(runSimulation(6, 0.12, 2000, 4063369934))
trials.append(runSimulation(6, 0.12, 2000, 5987411937))
print("\nTs = 0.12")
print("Calculated:")
pp.pprint({
    "w": 1.8,
    "q": 2.57,
    "Tw": .3,
    "Tq": .42,
    "util": .72
})
print("Simulated:")
pp.pprint({
    "w": "{:.2f}".format(np.mean([item[0]['w'] for item in trials])),
    "q": "{:.2f}".format(np.mean([item[0]['q'] for item in trials])),
    "Tw": "{:.2f}".format(np.mean([item[0]['Tw'] for item in trials])),
    "Tq": "{:.2f}".format(np.mean([item[0]['Tq'] for item in trials])),
    "util": "{:.2f}".format(np.mean([item[0]['util'] for item in trials]))
})
print("Confidence Intervals:")
pp.pprint({
    "w": "{:.2f} +- {:.2f}".format(trials[0][1]['w'][0], trials[0][1]['w'][1]),
    "q": "{:.2f} +- {:.2f}".format(trials[0][1]['q'][0], trials[0][1]['q'][1]),
    "Tw": "{:.2f} +- {:.2f}".format(trials[0][1]['Tw'][0], trials[0][1]['Tw'][1]),
    "Tq": "{:.2f} +- {:.2f}".format(trials[0][1]['Tq'][0], trials[0][1]['Tq'][1]),
    "util": "{:.2f} +- {:.2f}".format(trials[0][1]['util'][0], trials[0][1]['util'][1])
})

trials = []
trials.append(runSimulation(6, 0.16, 2000, 4522425020))
trials.append(runSimulation(6, 0.16, 2000, 8448481091))
trials.append(runSimulation(6, 0.16, 2000, 1834903635))
trials.append(runSimulation(6, 0.16, 2000, 4063369934))
trials.append(runSimulation(6, 0.16, 2000, 5987411937))
print("\nTs = 0.16")
print("Calculated:")
pp.pprint({
    "w": 23.04,
    "q": 24,
    "Tw": 3.84,
    "Tq": 4,
    "util": .96 
})
print("Simulated:")
pp.pprint({
    "w": "{:.2f}".format(np.mean([item[0]['w'] for item in trials])),
    "q": "{:.2f}".format(np.mean([item[0]['q'] for item in trials])),
    "Tw": "{:.2f}".format(np.mean([item[0]['Tw'] for item in trials])),
    "Tq": "{:.2f}".format(np.mean([item[0]['Tq'] for item in trials])),
    "util": "{:.2f}".format(np.mean([item[0]['util'] for item in trials]))
})
print("Confidence Intervals:")
pp.pprint({
    "w": "{:.2f} +- {:.2f}".format(trials[0][1]['w'][0], trials[0][1]['w'][1]),
    "q": "{:.2f} +- {:.2f}".format(trials[0][1]['q'][0], trials[0][1]['q'][1]),
    "Tw": "{:.2f} +- {:.2f}".format(trials[0][1]['Tw'][0], trials[0][1]['Tw'][1]),
    "Tq": "{:.2f} +- {:.2f}".format(trials[0][1]['Tq'][0], trials[0][1]['Tq'][1]),
    "util": "{:.2f} +- {:.2f}".format(trials[0][1]['util'][0], trials[0][1]['util'][1])
})

trials = []
trials.append(runSimulation(6, 0.20, 2000, 4522425020))
trials.append(runSimulation(6, 0.20, 2000, 8448481091))
trials.append(runSimulation(6, 0.20, 2000, 1834903635))
trials.append(runSimulation(6, 0.20, 2000, 4063369934))
trials.append(runSimulation(6, 0.20, 2000, 5987411937))
print("\nTs = 0.20")
print("Calculated:")
pp.pprint({
    "w": "approaches inf",
    "q": "approaches inf",
    "Tw": "approaches inf",
    "Tq": "approaches inf",
    "util": ">1"
})
print("Simulated:")
pp.pprint({
    "w": "{:.2f}".format(np.mean([item[0]['w'] for item in trials])),
    "q": "{:.2f}".format(np.mean([item[0]['q'] for item in trials])),
    "Tw": "{:.2f}".format(np.mean([item[0]['Tw'] for item in trials])),
    "Tq": "{:.2f}".format(np.mean([item[0]['Tq'] for item in trials])),
    "util": "{:.2f}".format(np.mean([item[0]['util'] for item in trials]))
})
print("Confidence Intervals:")
pp.pprint({
    "w": "{:.2f} +- {:.2f}".format(trials[0][1]['w'][0], trials[0][1]['w'][1]),
    "q": "{:.2f} +- {:.2f}".format(trials[0][1]['q'][0], trials[0][1]['q'][1]),
    "Tw": "{:.2f} +- {:.2f}".format(trials[0][1]['Tw'][0], trials[0][1]['Tw'][1]),
    "Tq": "{:.2f} +- {:.2f}".format(trials[0][1]['Tq'][0], trials[0][1]['Tq'][1]),
    "util": "{:.2f} +- {:.2f}".format(trials[0][1]['util'][0], trials[0][1]['util'][1])
})