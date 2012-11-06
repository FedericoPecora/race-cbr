#!/bin/sh

BASEDIR=$(dirname $0)
WRAPPER=$BASEDIR/../build/install/race_spatial_reasoner/bin/race_spatial_reasoner

if [ ! -x $WRAPPER ]; then
  echo "ERROR: $WRAPPER does not exist. Run gradle installApp first."
  exit 1
fi

$WRAPPER race_spatial_reasoner.spatialReasonerNode

