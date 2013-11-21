#!/bin/sh

BASEDIR=$(dirname $0)
WRAPPER=$BASEDIR/../build/install/race_hybrid_reasoner/bin/race_hybrid_reasoner

if [ ! -x $WRAPPER ]; then
  echo "ERROR: $WRAPPER does not exist. Run gradle installApp first."
  exit 1
fi

$WRAPPER RACEPR2HybridDispatcherService

