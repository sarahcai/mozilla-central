/* -*- Mode: C++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

#ifndef mozilla_MutationEvent_h__
#define mozilla_MutationEvent_h__

#include "mozilla/BasicEvents.h"
#include "nsCOMPtr.h"
#include "nsIAtom.h"
#include "nsIDOMNode.h"

namespace mozilla {

class InternalMutationEvent : public WidgetEvent
{
public:
  InternalMutationEvent(bool aIsTrusted, uint32_t aMessage) :
    WidgetEvent(aIsTrusted, aMessage, NS_MUTATION_EVENT),
    mAttrChange(0)
  {
    mFlags.mCancelable = false;
  }

  nsCOMPtr<nsIDOMNode> mRelatedNode;
  nsCOMPtr<nsIAtom>    mAttrName;
  nsCOMPtr<nsIAtom>    mPrevAttrValue;
  nsCOMPtr<nsIAtom>    mNewAttrValue;
  unsigned short       mAttrChange;

  void AssignMutationEventData(const InternalMutationEvent& aEvent,
                               bool aCopyTargets)
  {
    AssignEventData(aEvent, aCopyTargets);

    mRelatedNode = aEvent.mRelatedNode;
    mAttrName = aEvent.mAttrName;
    mPrevAttrValue = aEvent.mPrevAttrValue;
    mNewAttrValue = aEvent.mNewAttrValue;
    mAttrChange = aEvent.mAttrChange;
  }
};

// Bits are actually checked to optimize mutation event firing.
// That's why I don't number from 0x00.  The first event should
// always be 0x01.
#define NS_EVENT_BITS_MUTATION_SUBTREEMODIFIED                0x01
#define NS_EVENT_BITS_MUTATION_NODEINSERTED                   0x02
#define NS_EVENT_BITS_MUTATION_NODEREMOVED                    0x04
#define NS_EVENT_BITS_MUTATION_NODEREMOVEDFROMDOCUMENT        0x08
#define NS_EVENT_BITS_MUTATION_NODEINSERTEDINTODOCUMENT       0x10
#define NS_EVENT_BITS_MUTATION_ATTRMODIFIED                   0x20
#define NS_EVENT_BITS_MUTATION_CHARACTERDATAMODIFIED          0x40

} // namespace mozilla

#endif // mozilla_MutationEvent_h__
