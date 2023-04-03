import numpy as np
from scipy.optimize import linprog
import sys


def solve_simplex(a):
    b = -np.ones(a.shape[1])
    c = np.ones(a.shape[0])
    res = linprog(c, -a.T, b, method='highs')
    return res.x / res.fun


if __name__ == '__main__':
    num_rows = int(sys.stdin.readline())
    print(num_rows)
    rows = []
    row = 0
    for row in range(num_rows):
        rows.append(np.fromstring(sys.stdin.readline(), dtype=float, sep=' '))
    A = np.array(rows)
    print(' '.join([str(x) for x in solve_simplex(A)]))
