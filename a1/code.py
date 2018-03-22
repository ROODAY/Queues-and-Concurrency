import random
import math
import numpy.random
import collections
import pprint
import matplotlib.pyplot as plt

def exponential(lam):
	return math.log(1-random.random())/(-1 / lam)

def normal():
	return ((sum([random.random() for i in range(1000)]) - 1000 * 1/2) / math.sqrt(1000 * 1/12))

def arbitrary(table):
	u = random.random()
	for k,v in table.items():
		if u > k:
			u -= k
		else:
			return v

print("Part A:")
plt.hist([numpy.random.exponential() for i in range(1000)])
plt.title('Original Distribution')
plt.xlabel("Value")
plt.ylabel("Occurrences")
plt.show()
plt.hist([exponential(1) for i in range(1000)])
plt.title('Generated Distribution')
plt.xlabel("Value")
plt.ylabel("Occurrences")
plt.show()

print("Part B:")
plt.hist([numpy.random.normal() for i in range(1000)])
plt.title('Original Distribution')
plt.xlabel("Value")
plt.ylabel("Occurrences")
plt.show()
plt.hist([normal() for i in range(1000)])
plt.title('Generated Distribution')
plt.xlabel("Value")
plt.ylabel("Occurrences")
plt.show()

print("Part C:")
table = {
	0.125: 3,
	0.23438: 5,
	0.14648: 7,
	0.14063: 8,
	0.03052: 9,
	0.17578: 10,
	0.05493: 12,
	0.05273: 13,
	0.03296: 15,
	0.00659: 18
}
pp = pprint.PrettyPrinter(indent=4)
samps = [arbitrary(table) for i in range(10000)]
t = collections.Counter(samps)
newt = {}
for k,v in t.items():
	newt[v/10000] = k
print("Original Distribution:")
pp.pprint(table)
print("Generated Distribution:")
pp.pprint(newt)