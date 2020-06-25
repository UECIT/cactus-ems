export function firstGroupedBy<T>(items: T[], key: (arg:T) => any) : T[] {
    return items.reduce(function(r, a) {
      if (!r.some(item => key(item) == key(a))) {
          r.push(a);
      }
      return r;
    }, []);
}